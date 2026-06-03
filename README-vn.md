# User Service

[English](README.md) | **Tiếng Việt**

> Human Identity Domain Service cho Xime Base Platform — quản lý tài khoản người dùng, credential, trạng thái tài khoản và dữ liệu hồ sơ cá nhân.

---

User Service là **tầng định danh con người** của Xime Base Platform. Nó là nguồn tin cậy cho mọi thứ thuộc về một tài khoản người dùng — credential, trạng thái tài khoản và thông tin hồ sơ. Nó không cấp token hay quản lý session; đó là trách nhiệm của Identity Service.

```
Client
  ↓ login / register
Identity Service     ← authentication gateway, cấp JWT
  ↓ VerifyCredential / RegisterUser (gRPC + mTLS)
User Service         ← credential owner, trạng thái tài khoản, profile
  ↓
PostgreSQL
```

---

## User Service làm gì

**Quản lý Credential**
- Sở hữu và xác minh credential người dùng (password, OAuth, passkey, API key)
- Hash password khi lưu trữ (BCrypt / Argon2id)
- Kiểm tra tính hợp lệ của credential khi đăng nhập

**Trạng thái Tài khoản**
- Duy trì vòng đời tài khoản: `ACTIVE`, `LOCKED`, `DISABLED`, `BANNED`, `PENDING_VERIFICATION`
- Kiểm tra trạng thái trước khi trả về kết quả xác minh thành công
- Là cơ quan có thẩm quyền về việc tài khoản người dùng có được phép đăng nhập không

**Hồ sơ Người dùng**
- Lưu trữ và cung cấp dữ liệu hồ sơ: tên, avatar, tiểu sử, ngày sinh, ngôn ngữ
- Quản lý thông tin liên hệ (email, số điện thoại) với trạng thái xác minh
- Quản lý địa chỉ, liên kết mạng xã hội và sở thích

## User Service KHÔNG làm gì

- Không cấp JWT token — đó là Identity Service
- Không quản lý session hay refresh token
- Không xử lý phân quyền platform hay RBAC
- Không chứa social graph, bài đăng, đơn hàng hay dữ liệu nghiệp vụ bất kỳ
- Không expose Credential API ra ngoài — chỉ internal gRPC + mTLS

---

## Quyết định thiết kế quan trọng

### Identifier Normalization

Tất cả identifier phải được normalize trước khi lưu hoặc tìm kiếm:

```
Email:    TEST@Mail.com     → test@mail.com
Phone:    +84 123 456 789  → 84123456789
Username: → lowercase + trim + unicode normalization
```

Không normalize dẫn đến identifier trùng lặp, lỗi routing và shard mismatch.

### Immutable Shard Placement

Người dùng được gán vào shard tại thời điểm đăng ký và không bao giờ di chuyển. Toàn bộ auth data, profile và contact đều nằm trên cùng một shard để tối đa hóa locality và giảm thiểu cross-shard call.

### Credential API — Chỉ Nội Bộ

Xác minh credential và đăng ký người dùng chỉ được expose qua **gRPC + mTLS**. Không có REST endpoint public nào cho các thao tác này. Caller (Identity Service) được xác thực qua client certificate.

### PasswordHash trong User (Tạm thời)

Hiện tại `passwordHash` nằm trực tiếp trong `User` aggregate. Đây là thiết kế có chủ đích cho giai đoạn hiện tại. Thiết kế dự kiến tách thành `Credential` aggregate riêng trong tương lai, hỗ trợ nhiều loại credential trên một user.

---

## Chạy nhanh

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

REST: `8083` | gRPC: `9092`

Yêu cầu PostgreSQL tại `localhost:5432/user_service` và Trust Service (để bootstrap mTLS).

---

## Kiến trúc

User Service theo **Hexagonal Architecture** với DDD tactical patterns, xây dựng trên Spring Boot 4 / Java 25:

```
src/main/java/
├── api/            ← Input adapters: gRPC (nội bộ), REST (bên ngoài)
├── application/    ← Use case, service, port, DTO, mapper
├── domain/         ← Business model thuần, factory, domain service
└── infrastructure/ ← JPA persistence, crypto, gRPC client, security
```

Repository interface nằm ở `application/port/out/` (không phải trong `domain/`). Domain service là Plain Java không có Spring annotation.

---

## Tài liệu

| Tài liệu | Mô tả |
|---|---|
| [Tổng quan](docs/vn/overview.md) | Vai trò, ranh giới, vị trí trong Base Platform |
| [Kiến trúc](docs/vn/architecture.md) | Cấu trúc tầng, DDD pattern, cây thư mục |
| [Identity System](docs/vn/identity-system.md) | Trạng thái tài khoản, credential model, identifier normalization |
| [API Reference](docs/vn/api.md) | Định nghĩa gRPC internal API và quy tắc sử dụng |
| [Tích hợp](docs/vn/integration.md) | Identity Service tích hợp với User Service như thế nào |

---

## Các Service trong Base Platform

| Service | Vai trò |
|---|---|
| `trust-service` | Trust infrastructure — CA, mTLS, JWT signing key |
| `identity-service` | Authentication infrastructure — JWT, refresh token |
| `user-service` | **Human Identity Domain Service** |
| `data-service` | Data infrastructure — object storage, permission |
| `notification-service` | Gửi thông báo |
| `payment-service` | Thanh toán |

---

## Trạng thái dự án

User Service đang trong **giai đoạn phát triển tích cực**. Luồng xác minh credential (đăng nhập) và đăng ký người dùng đã được implement. Profile CRUD, quản lý contact và mTLS bootstrap đang hoạt động.

---

## Giấy phép

MIT
