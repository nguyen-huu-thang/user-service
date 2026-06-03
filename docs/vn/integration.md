# Tích Hợp

[English](../en/integration.md) | **Tiếng Việt**

---

## Tổng Quan

User Service tích hợp với hai service khác trong Base Platform:

| Tích hợp | Chiều | Khi nào |
|---|---|---|
| Xác minh credential | Identity Service → User Service | Mỗi lần đăng nhập |
| Đăng ký người dùng | Identity Service → User Service | Mỗi lần tạo tài khoản mới |
| Lấy JWT public key | User Service → Trust Service | Startup + refresh định kỳ |
| Chứng chỉ mTLS | User Service → Trust Service | Bootstrap + cert rotation |

---

## Tích Hợp với Identity Service

Identity Service là **service duy nhất** được ủy quyền gọi gRPC API nội bộ của User Service. Mọi thao tác credential đều đi qua đường dẫn này.

### Luồng Đăng Nhập

```
Client gửi request đăng nhập
      ↓
Identity Service
  1. nhận request đăng nhập (email/phone/username + password)
  2. normalize identifier
  3. resolve shard_id qua Search Service
  4. gọi User Service[shard_id].VerifyCredential (gRPC + mTLS)
      ↓
User Service
  5. normalize identifier (phòng thủ)
  6. resolve user từ DB shard local
  7. verify password với hash đã lưu
  8. kiểm tra trạng thái tài khoản (ACTIVE / LOCKED / DELETED)
  9. trả về VerifyCredentialResponse
      ↓
Identity Service
  10. thành công: cấp JWT với {user_id, shard_id, subject_type}
  11. thất bại: map failure_reason thành HTTP response phù hợp
```

User Service không bao giờ biết về cấu trúc JWT. Identity Service không bao giờ lưu mật khẩu.

### Luồng Đăng Ký

```
Client gửi request đăng ký
      ↓
Identity Service
  1. nhận request đăng ký
  2. normalize identifier
  3. phân bổ shard_id (quyết định routing)
  4. gọi User Service[shard_id].RegisterUser (gRPC + mTLS)
      ↓
User Service
  5. normalize identifier (phòng thủ)
  6. kiểm tra identifier availability trên shard local
  7. hash password
  8. tạo User domain model + UserContact
  9. lưu vào cơ sở dữ liệu
  10. trả về RegisterUserResponse {identity_id, shard_id, created_at}
      ↓
Identity Service
  11. cấp JWT ban đầu
  12. tạo refresh token
  13. trả về token pair cho client
```

---

## Tích Hợp với Trust Service

User Service tích hợp với Trust Service cho hai mục đích: verify JWT local và tham gia mTLS với tư cách một service.

### Lấy JWT Public Key

User Service verify JWT inbound (cho REST endpoint bên ngoài) local bằng public key lấy từ Trust Service:

```
User Service khởi động
      ↓
  Lấy public key từ Trust Service (GetPublicKeys)
      ↓
  Cache key set trong memory (KeyContext)
      ↓
  Request REST inbound có JWT Bearer token
      ↓
  Trích xuất kid từ JWT header
      ↓
  Tìm key trong cache local theo kid
      ↓
  Verify JWT signature local — KHÔNG gọi Trust Service
```

Refresh public key chạy theo lịch background. Trust Service down không ảnh hưởng verify JWT miễn là cache còn ấm.

### Bootstrap Chứng Chỉ mTLS

User Service tham gia mTLS cả với tư cách client (gọi service khác) lẫn server (nhận gọi từ Identity Service):

```
Lần triển khai đầu tiên:
  1. Platform admin cấp cert ban đầu qua Trust Service admin API
  2. User Service load cert + private key từ bootstrap file
  3. Mọi kết nối gRPC inbound đều yêu cầu client cert từ cùng Root CA

Cert rotation (mỗi ~100 ngày):
  1. User Service phát hiện cert sắp đến ngưỡng rotation
  2. Tạo key pair mới local
  3. Gọi Trust Service.RotateCertificate với refresh token + public key mới
  4. Nhận cert mới + refresh token mới
  5. Reload cấu hình TLS với cert mới
```

Cấu hình bootstrap trong `application.yml`:

```yaml
user:
  bootstrap:
    service-id: user-service
    shard-id: U00000
    path: ./runtime/security/bootstrap.txt
```

File `bootstrap.txt` chứa chứng chỉ ban đầu và refresh token được Trust Service cấp trong quá trình triển khai.

---

## Tích Hợp với Application Service

Application service (mạng xã hội, thương mại điện tử, SaaS) tương tác với User Service chỉ để lấy **dữ liệu profile**, không bao giờ lấy credential data.

```
Application Service (ví dụ: post-service)
  ↓ cần hiển thị tên tác giả + avatar bài đăng
  GET /api/v1/profile/{userId}
  ↓ (HTTPS + JWT)
User Service External REST API
  ↓ trả về: display_name, avatar_url, bio
```

Application service không được gọi gRPC endpoint nội bộ. Nên cache dữ liệu profile ít thay đổi (tên hiển thị, URL avatar) để giảm tải lên User Service.

---

## Tích Hợp với Search Service (Kế Hoạch)

User Service hiện tại không duy trì bảng identifier index toàn cục. Tìm user theo email hoặc số điện thoại qua các shard cần Search Service:

```
identifier (email / phone)
      ↓
Search Service          ← duy trì mapping identifier → shard_id toàn cục
      ↓
resolve (shard_id, user_id)
      ↓
caller route đến User Service[shard_id]
```

Khi user đăng ký, User Service sẽ publish event để Search Service tiêu thụ và cập nhật index. Đến lúc đó, identifier routing được xử lý bởi mapping nội bộ của Identity Service.

---

## Tóm Tắt Tích Hợp

```
Trust Service
  ↓ public key (startup + định kỳ)
  ↓ mTLS cert (bootstrap + rotation)

User Service
  ↑ VerifyCredential (mỗi lần đăng nhập)
  ↑ RegisterUser (mỗi lần đăng ký)
Identity Service

User Service
  ↓ dữ liệu profile (theo yêu cầu)
Application Services (post-service, order-service, v.v.)
```

User Service **nằm trong critical path của đăng nhập và đăng ký** — tính khả dụng của nó ảnh hưởng trực tiếp đến authentication người dùng. Nó không nằm trong critical path của JWT verification (luôn diễn ra local).
