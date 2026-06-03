# Identity System

[English](../en/identity-system.md) | **Tiếng Việt**

---

## Tổng Quan

Identity system trong User Service có ba thành phần phối hợp để trả lời câu hỏi: *"Người dùng này có thể đăng nhập không, và họ là ai?"*

| Thành phần | Trách nhiệm |
|---|---|
| **Identifier Normalization** | Đảm bảo mọi identifier ở dạng chuẩn trước khi lưu hoặc tra cứu |
| **Credential Verification** | Xác nhận người dùng biết mật khẩu (hoặc credential tương đương) |
| **Account State** | Xác định tài khoản có được phép đăng nhập không |

---

## Identifier Normalization

### Tại Sao Quan Trọng

Người dùng có thể đăng ký với `TEST@MAIL.COM` và đăng nhập với `test@mail.com`. Nếu không normalize, hai cái này được xử lý như identifier khác nhau — đăng nhập thất bại, hoặc tạo ra tài khoản trùng lặp.

Quy tắc normalize:

| Loại Identifier | Quy tắc | Ví dụ |
|---|---|---|
| Email | Lowercase toàn bộ địa chỉ | `TEST@Mail.com` → `test@mail.com` |
| Phone | Bỏ định dạng, dùng E.164 không có `+` | `+84 123 456 789` → `84123456789` |
| Username | Lowercase + trim + unicode normalization | `  User_Ñame  ` → `user_ñame` |

### Normalization Xảy Ra Ở Đâu

`IdentifierNormalizer` là **domain service** — plain Java, không phụ thuộc Spring:

```java
public class IdentifierNormalizer {
    public String normalize(String identifier, IdentifierType type) {
        return switch (type) {
            case EMAIL    -> identifier.trim().toLowerCase();
            case PHONE    -> normalizePhone(identifier);
            case USERNAME -> identifier.trim().toLowerCase();
        };
    }
}
```

Normalization được áp dụng tại:
- `LoginUseCase` trước khi resolve user
- `RegisterUseCase` trước khi kiểm tra tính khả dụng và lưu
- Mọi entry point — dù caller (Identity Service) đã normalize hay chưa

Normalize hai lần ở User Service là phòng thủ: bảo vệ khỏi bug của caller và inconsistency cross-service.

### Normalization KHÔNG Phải Tùy Chọn

Identifier được lưu mà không normalize là vi phạm tính toàn vẹn dữ liệu. `IdentifierNormalizer` phải được gọi trước mọi thao tác lưu hoặc query liên quan đến identifier. Quy tắc này được ghi trong `architecture-rules.md` và được thực thi qua code review.

---

## Credential System

### Trạng Thái Hiện Tại (Stage 1)

Trong implementation hiện tại, password hash nằm trực tiếp trong `User` aggregate:

```
bảng users:
  id            BYTEA      ← KSUID
  username      VARCHAR
  password_hash TEXT       ← BCrypt hash (vị trí tạm thời)
  status        VARCHAR
  created_at    TIMESTAMP
  updated_at    TIMESTAMP
```

Đây là sự thỏa hiệp kiến trúc có chủ đích. Hoạt động đúng nhưng không scale được cho nhiều loại credential (OAuth, passkey, API key, MFA) mà không làm User aggregate phình to.

### Thiết Kế Tương Lai (Stage 2)

`Credential` aggregate được lên kế hoạch tách biệt lưu trữ credential khỏi trạng thái tài khoản:

```
bảng users:              ← chỉ chứa trạng thái tài khoản
  id, username, status, created_at, updated_at

bảng user_credentials:   ← một dòng cho mỗi loại credential
  id            BYTEA
  user_id       BYTEA  → users.id
  type          VARCHAR  ← PASSWORD / OAUTH_GOOGLE / OAUTH_GITHUB / PASSKEY / API_KEY / CERTIFICATE
  value         TEXT     ← hash, token, public key — tùy loại
  metadata      JSONB    ← provider ID, thông tin thiết bị, v.v.
  created_at    TIMESTAMP
  updated_at    TIMESTAMP
```

Cách này cho phép một user có nhiều loại credential mà không cần thay đổi schema. Migration từ Stage 1 sang Stage 2 chuyển `password_hash` từ `users` sang `user_credentials`.

### Các Loại Credential Hỗ Trợ

| Loại | Mô tả | Trạng thái |
|---|---|---|
| `PASSWORD` | BCrypt hoặc Argon2id hash | Đã implement |
| `OAUTH_GOOGLE` | Google OAuth provider mapping | Kế hoạch |
| `OAUTH_GITHUB` | GitHub OAuth provider mapping | Kế hoạch |
| `PASSKEY` | FIDO2 WebAuthn public key | Kế hoạch |
| `API_KEY` | API key machine-to-machine | Kế hoạch |
| `CERTIFICATE` | Client certificate cho service account | Kế hoạch |

### Password Hashing

Password hashing được ủy quyền cho `PasswordHasher` port:

```java
public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String hash);
}
```

Implementation hiện tại (`BCryptPasswordHasher`) dùng BCrypt với cost factor mặc định. Abstraction này cho phép chuyển sang Argon2id mà không thay đổi use case.

---

## Account State

### Mô Hình Trạng Thái

```
ACTIVE               ← có thể đăng nhập, hoạt động bình thường
LOCKED               ← tạm thời bị ngăn đăng nhập (hành động admin hoặc trigger bảo mật)
DISABLED             ← tài khoản tự vô hiệu hóa hoặc bị admin vô hiệu hóa
BANNED               ← hạn chế vĩnh viễn (vi phạm chính sách)
PENDING_VERIFICATION ← đã đăng ký nhưng email/phone chưa được xác minh
DELETED              ← soft-deleted, không thể khôi phục
```

### Chuyển Đổi Trạng Thái

```
PENDING_VERIFICATION → ACTIVE      (xác minh hoàn tất)
ACTIVE              → LOCKED       (trigger bảo mật hoặc admin)
ACTIVE              → DISABLED     (tự phục vụ hoặc admin)
ACTIVE              → BANNED       (thực thi chính sách)
ACTIVE/LOCKED/DISABLED → DELETED   (yêu cầu xóa dữ liệu)
LOCKED              → ACTIVE       (admin mở khóa)
DISABLED            → ACTIVE       (kích hoạt lại)
```

State change được mô hình hóa như thao tác immutable trên `User`:

```java
User locked  = user.lock(now);     // trả về User mới với status LOCKED
User active  = user.activate(now); // trả về User mới với status ACTIVE
User deleted = user.delete(now);   // trả về User mới với status DELETED
```

User đã bị xóa không thể kích hoạt lại. Không có chuyển đổi trạng thái nào từ `DELETED` được cho phép.

### Kiểm Tra Trạng Thái Trong Luồng Đăng Nhập

```
credential đã xác minh?
      ↓ có
tài khoản DELETED?   → trả về thất bại: ACCOUNT_DELETED
      ↓ không
tài khoản LOCKED?    → trả về thất bại: ACCOUNT_LOCKED
      ↓ không
tài khoản ACTIVE?    → trả về thành công với VerifiedIdentity
```

Trạng thái disabled và banned theo cùng pattern — mỗi cái trả về một failure_reason cụ thể để Identity Service có thể cung cấp thông báo phù hợp cho người dùng.

---

## Identifier Routing và Sharding

### Shard Assignment Cố Định

Khi người dùng đăng ký, họ được gán vào một shard. Sự gán này là vĩnh viễn. Toàn bộ dữ liệu của người dùng (auth, profile, contact, address) đều nằm trên cùng shard.

```
Lúc đăng ký:
  Identity Service phân bổ shard → user.shard_id = "U00000"
  User Service shard "U00000" tạo user record

Lúc query:
  caller phải biết shard_id để route trực tiếp đến đúng User Service instance
  hoặc → Search Service resolve identifier → shard_id → direct routing
```

### Tìm User Theo Identifier

User Service không duy trì bảng lookup toàn cục. Tìm user từ email hoặc số điện thoại qua các shard cần Search Service:

```
email/phone → Search Service          ← duy trì mapping identifier → shard_id toàn cục
      ↓
resolve (shard_id, user_id)
      ↓
caller route đến User Service[shard_id]
```

User Service chỉ xử lý lookup trong shard của mình. Query cross-shard đi qua Search Service.

### Shard ID Trong Luồng Đăng Nhập

Request `VerifyCredential` bao gồm `shard_id`:

```protobuf
message VerifyCredentialRequest {
  string identifier  = 1;
  IdentifierType identifier_type = 2;
  string credential  = 3;
  CredentialType credential_type = 4;
  string user_agent  = 5;
  string shard_id    = 6;  ← routing hint: gọi đến User Service instance nào
}
```

Identity Service resolve shard trước khi gọi User Service, đảm bảo request đến đúng instance.
