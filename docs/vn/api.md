# API Reference

[English](../en/api.md) | **Tiếng Việt**

---

## Tổng Quan

User Service expose hai nhóm API:

| Nhóm | Transport | Xác thực | Mục đích |
|---|---|---|---|
| **gRPC nội bộ** | gRPC + mTLS | Client certificate (Identity Service) | Xác minh credential, đăng ký user |
| **REST bên ngoài** | HTTPS | JWT Bearer token | Đọc và cập nhật profile |

Proto file nằm tại `src/main/proto/exposed/`:
- `internal/authentication/` — định nghĩa gRPC service nội bộ
- `external/` — định nghĩa API bên ngoài (nếu có)

---

## gRPC API Nội Bộ

Tất cả API nội bộ đều yêu cầu **mTLS**. Client certificate phải được cấp bởi Root CA của platform (Trust Service). Chỉ Identity Service mới được ủy quyền gọi các endpoint này.

### LoginService

```protobuf
service LoginService {
  // Xác minh credential và trạng thái tài khoản.
  // Được Identity Service gọi trong luồng đăng nhập.
  rpc VerifyCredential (VerifyCredentialRequest) returns (VerifyCredentialResponse);
}
```

**VerifyCredentialRequest**

```protobuf
message VerifyCredentialRequest {
  string identifier      = 1;  // identifier đã normalize (email / phone / username)
  IdentifierType identifier_type = 2;
  string credential      = 3;  // raw credential — KHÔNG BAO GIỜ log trường này
  CredentialType credential_type = 4;
  string user_agent      = 5;  // metadata thiết bị / client
  string shard_id        = 6;  // routing hint shard đích
}
```

**VerifyCredentialResponse**

```protobuf
message VerifyCredentialResponse {
  bool success           = 1;  // true nếu credential hợp lệ và tài khoản được phép
  VerifiedIdentityDto identity = 2;  // chỉ có khi success = true
  string failure_reason  = 3;  // IDENTITY_NOT_FOUND / INVALID_CREDENTIAL / ACCOUNT_LOCKED / ACCOUNT_DELETED
  bool locked            = 4;
  bool disabled          = 5;
  bool requires_mfa      = 6;
}
```

**Failure Reason**

| Code | Ý nghĩa |
|---|---|
| `IDENTITY_NOT_FOUND` | Không tìm thấy user với identifier đó trên shard này |
| `INVALID_CREDENTIAL` | Tìm thấy user nhưng mật khẩu không khớp |
| `ACCOUNT_LOCKED` | Tài khoản tồn tại, credential khớp, nhưng tài khoản bị khóa |
| `ACCOUNT_DELETED` | Tài khoản đã bị soft-deleted |

---

### RegistrationService

```protobuf
service RegistrationService {
  // Tạo tài khoản người dùng mới.
  // Được Identity Service gọi trong luồng đăng ký.
  rpc RegisterUser (RegisterUserRequest) returns (RegisterUserResponse);
}
```

**RegisterUserRequest**

```protobuf
message RegisterUserRequest {
  string identifier      = 1;  // identifier đã normalize
  IdentifierType identifier_type = 2;
  string credential      = 3;  // raw credential — KHÔNG BAO GIỜ log trường này
  CredentialType credential_type = 4;
  string user_agent      = 5;
  string shard_id        = 6;  // shard đích đã phân bổ
}
```

**RegisterUserResponse**

```protobuf
message RegisterUserResponse {
  string identity_id = 1;  // user ID mới (KSUID, cũng chính là user ID)
  string shard_id    = 2;  // shard đã gán
  int64  created_at  = 3;  // epoch millis
}
```

---

## Các Kiểu Dùng Chung

### VerifiedIdentityDto

Trả về khi xác minh credential thành công. Chứa thông tin tối thiểu Identity Service cần để cấp JWT.

```protobuf
message VerifiedIdentityDto {
  string identity_id  = 1;  // user ID (KSUID)
  string subject_type = 2;  // luôn là "HUMAN" cho User Service
  string shard_id     = 3;  // shard nơi user tồn tại
  string service_id   = 4;  // luôn là "user-service"
  string tenant_id    = 5;  // tùy chọn — cho ứng dụng multi-tenant
}
```

### IdentifierType

```protobuf
enum IdentifierType {
  IDENTIFIER_TYPE_UNSPECIFIED = 0;
  USERNAME = 1;
  EMAIL    = 2;
  PHONE    = 3;
}
```

### CredentialType

```protobuf
enum CredentialType {
  CREDENTIAL_TYPE_UNSPECIFIED = 0;
  PASSWORD     = 1;
  PASSKEY      = 2;
  OAUTH_GOOGLE = 3;
  OAUTH_GITHUB = 4;
  API_KEY      = 5;
  CERTIFICATE  = 6;
}
```

---

## REST API Bên Ngoài

REST endpoint bên ngoài được xác thực bằng **JWT Bearer token** do Identity Service cấp. Chỉ dành cho các thao tác profile không nhạy cảm.

### Profile Endpoints

```
GET    /api/v1/profile/{userId}           ← lấy profile người dùng
PUT    /api/v1/profile/{userId}           ← cập nhật profile người dùng
GET    /api/v1/profile/{userId}/contacts  ← lấy danh sách contact
POST   /api/v1/profile/{userId}/contacts  ← thêm contact
GET    /api/v1/profile/{userId}/addresses ← lấy danh sách địa chỉ
```

Profile endpoint không bao giờ expose dữ liệu credential (password hash, oauth token, v.v.).

---

## Quy Tắc Sử Dụng API

### Credential API (Nội bộ)

- **Không bao giờ gọi từ application service** — chỉ Identity Service mới được gọi `LoginService` và `RegistrationService`
- Trường `credential` trong request phải **không bao giờ được log** ở bất kỳ tầng nào
- Luôn truyền `identifier` đã normalize — User Service normalize lại nhưng caller nên normalize trước
- Bao gồm `shard_id` hợp lệ — routing đến sai shard sẽ trả về `IDENTITY_NOT_FOUND`

### Profile API (Bên ngoài)

- Xác thực mọi request với JWT hợp lệ
- User Service verify JWT local bằng public key đã cache từ Trust Service
- Profile endpoint an toàn khi gọi từ application service và frontend

### Xử Lý Lỗi

| gRPC Status | Ý nghĩa |
|---|---|
| `OK` | Request đã được xử lý — kiểm tra trường `success` trong response |
| `INVALID_ARGUMENT` | Thiếu hoặc malformed request field |
| `UNAUTHENTICATED` | mTLS cert thiếu hoặc không hợp lệ |
| `PERMISSION_DENIED` | mTLS cert hợp lệ nhưng caller không được ủy quyền |
| `INTERNAL` | Lỗi server không mong đợi |

Lưu ý: xác minh credential thất bại trả về `OK` status với `success = false` và `failure_reason` — đây không phải lỗi ở gRPC level.
