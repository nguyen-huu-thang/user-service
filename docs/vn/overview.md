# Tổng Quan

[English](../en/overview.md) | **Tiếng Việt**

---

## User Service là gì?

User Service là **Human Identity Domain Service** của Xime Base Platform.

Vai trò của nó là trở thành nguồn tin cậy duy nhất cho mọi thứ xác định một tài khoản người dùng trên platform — credential chứng minh danh tính, trạng thái tài khoản xác định quyền hành động, và dữ liệu hồ sơ mô tả người dùng. Không service nào khác sở hữu hay sao chép dữ liệu này.

```
Client (mobile, web, third-party)
      ↓ đăng nhập / đăng ký
Identity Service   ← authentication gateway, cấp JWT
      ↓ VerifyCredential / RegisterUser  (gRPC + mTLS, chỉ nội bộ)
User Service       ← credential owner, cơ quan trạng thái tài khoản, profile store
      ↓
PostgreSQL  (cơ sở dữ liệu per-shard)
```

---

## Vị trí trong Base Platform

Xime Base Platform chia làm hai tầng:

### Base Platform (core services)

Các service hạ tầng chung, tái sử dụng được, xây dựng một lần và dùng cho mọi ứng dụng:

| Service | Vai trò |
|---|---|
| `trust-service` | Trust infrastructure — CA, mTLS, JWT signing key |
| `identity-service` | Authentication — cấp JWT, refresh token, session |
| `user-service` | **Human Identity Domain — credential, trạng thái tài khoản, profile** |
| `data-service` | Data infrastructure — object storage, permission |
| `notification-service` | Gửi thông báo |
| `payment-service` | Thanh toán |

### Application Layer (business services)

Logic nghiệp vụ cụ thể xây dựng trên nền Base Platform:

- **Mạng xã hội**: post-service, comment-service, media-service
- **Thương mại điện tử**: product-service, order-service
- **SaaS / AI**: workspace-service, dataset-service, ai-agent-service

Các Application Service tham chiếu đến định danh người dùng nhưng không bao giờ sao chép nó. Chúng gọi User Service (qua Identity Service hoặc trực tiếp với quyền phù hợp) để lấy dữ liệu định danh người dùng.

---

## Hai Domain trong Một Service

User Service quản lý hai domain rõ ràng tách biệt, chia sẻ user ID nhưng khác nhau về độ nhạy cảm và pattern truy cập:

### A. Authentication Domain (nhạy cảm)

Tất cả thứ cần thiết để xác thực người dùng và xác định xem có được phép đăng nhập không:

```
User Service (authentication domain)
  ├── credential: passwordHash, oauth mapping, passkey
  ├── trạng thái tài khoản: ACTIVE / LOCKED / DISABLED / BANNED / PENDING_VERIFICATION
  └── authentication metadata: token version, last login, device trust
```

- Chỉ expose qua **gRPC + mTLS nội bộ** (không có REST public)
- Identity Service gọi trong luồng đăng nhập và đăng ký
- Rất nhạy cảm — không bao giờ log, không bao giờ expose trực tiếp ra application layer

### B. Profile Domain (không nhạy cảm)

Tất cả thứ mô tả người dùng về mặt hiển thị và xã hội:

```
User Service (profile domain)
  ├── profile: họ tên, tên hiển thị, avatar, tiểu sử, ngày sinh, giới tính
  ├── contact: email (đã xác minh/chính), số điện thoại (đã xác minh/chính)
  ├── address: địa chỉ giao hàng/thanh toán
  ├── links: GitHub, Twitter, LinkedIn, website
  └── interests: tag sở thích do người dùng chọn
```

- Expose qua **REST API bên ngoài** (xác thực bằng JWT)
- Application service gọi để render thông tin người dùng
- Độ nhạy cảm thấp hơn — an toàn để cache và expose cho frontend

---

## Triết lý thiết kế

| Câu hỏi | Câu trả lời |
|---|---|
| User Service là gì? | Human Identity Domain Service — credential owner, cơ quan tài khoản |
| Nó sở hữu gì? | Credential, trạng thái tài khoản, hồ sơ người dùng |
| Nó có cấp JWT không? | Không — Identity Service là authentication gateway |
| Nó có quản lý session không? | Không — vòng đời session thuộc về Identity Service |
| Credential API có public không? | Không — chỉ gRPC + mTLS nội bộ |
| Profile nằm ở đâu? | User Service — phục vụ qua REST API bên ngoài |
| Nếu nó bị down thì sao? | JWT verify vẫn hoạt động; đăng nhập và đăng ký sẽ thất bại cho đến khi phục hồi |

---

## Quan hệ với Identity Service

User Service và Identity Service là **hai service riêng biệt với hợp đồng rõ ràng**:

```
Trách nhiệm Identity Service:
  - nhận request đăng nhập/đăng ký từ client
  - normalize identifier
  - gọi User Service để verify credential và trạng thái tài khoản
  - cấp JWT nếu verification thành công
  - quản lý vòng đời refresh token

Trách nhiệm User Service:
  - xác minh credential khớp với dữ liệu đã lưu
  - kiểm tra tài khoản có được phép đăng nhập không
  - trả về verified identity data (user ID, shard, subject type)
  - sở hữu và cập nhật credential data
```

User Service không bao giờ tạo hay validate JWT. Identity Service không bao giờ lưu mật khẩu hay trạng thái tài khoản.

---

## User Service KHÔNG phải là gì

- Không phải authentication gateway — không xử lý trực tiếp request đăng nhập từ client
- Không phải token issuer — không có hiểu biết về cấu trúc JWT
- Không phải session manager — không theo dõi session đang hoạt động
- Không phải business domain service — không chứa logic mạng xã hội, thương mại hay ứng dụng
- Không phải global directory — mỗi instance chỉ quản lý một shard
