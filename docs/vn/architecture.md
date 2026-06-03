# Kiến Trúc

[English](../en/architecture.md) | **Tiếng Việt**

---

## Tổng Quan Tầng

User Service theo **Hexagonal Architecture** (Ports and Adapters) với DDD tactical patterns, xây dựng trên Spring Boot 4 / Java 25.

```
External Clients (gRPC nội bộ, REST bên ngoài)
        ↓
   Adapter Layer (api/)           ← nhận request, map sang use case input
        ↓
 Application Layer (application/) ← use case, service, orchestration
        ↓
   Domain Layer (domain/)         ← business model thuần, không phụ thuộc framework
        ↑
Infrastructure Layer (infrastructure/) ← JPA, crypto, gRPC client, security
```

Domain layer không có kiến thức về Spring, JPA hay gRPC. Infrastructure layer không có kiến thức về use case logic. Dependency luôn hướng vào trong về phía domain.

---

## Cấu Trúc Thư Mục

```
src/main/java/vn/xime/user/
│
├── api/                                    ← adapter layer
│   ├── grpc/
│   │   ├── identity/                       ← gRPC handler nội bộ
│   │   │   ├── LoginGrpcApi               ← xử lý VerifyCredential
│   │   │   └── RegistrationGrpcApi        ← xử lý RegisterUser
│   │   └── mapper/                        ← DTO ↔ Protobuf mappers (MapStruct)
│   │       ├── LoginGrpcMapper
│   │       └── RegistrationGrpcMapper
│   └── rest/
│       └── external/profile/              ← REST handler bên ngoài (xác thực JWT)
│
├── application/
│   ├── usecase/                           ← orchestrate: validate → domain → repo → return
│   │   └── identity/
│   │       ├── LoginUseCase               ← verify credential, check account state
│   │       ├── RegisterUseCase            ← tạo user + credential + contact
│   │       └── RecoveryUseCase            ← luồng phục hồi tài khoản
│   ├── service/                           ← application service (Spring bean)
│   │   └── identity/
│   │       ├── ResolveUserByIdentifierService  ← tìm user theo email/phone/username
│   │       └── CheckIdentifierAvailabilityService
│   ├── port/                              ← interface port vào / ra
│   │   └── out/
│   │       ├── user/        UserRepository, UserUsernameHistoryRepository
│   │       ├── profile/     UserProfileRepository
│   │       ├── contact/     UserContactRepository
│   │       ├── address/     UserAddressRepository, UserLinkRepository
│   │       ├── interest/    InterestRepository, UserInterestRepository
│   │       ├── crypto/      PasswordHasher
│   │       ├── security/    JwtTokenVerifier
│   │       └── integration/ ResolveVerificationKey
│   ├── dto/                               ← input/output DTO
│   │   └── external/
│   │       ├── authentication/  VerifyCredentialRequest/Response
│   │       └── identity/        RegisterUserRequest/Response
│   └── mapper/                            ← Domain ↔ DTO mappers (MapStruct)
│
├── domain/
│   ├── user/
│   │   ├── model/           User, UserStatus, UserUsernameHistory
│   │   └── factory/         UserFactory, UserUsernameHistoryFactory, ...
│   ├── authentication/
│   │   ├── model/           IdentifierType, CredentialType, VerifiedIdentity, JwtClaims, KeyContext
│   │   └── service/         IdentifierNormalizer  ← domain service, plain Java
│   ├── credential/
│   │   └── model/           CredentialType
│   ├── profile/
│   │   ├── model/           UserProfile, Gender
│   │   └── service/         UserProfileValidationService
│   ├── address/
│   │   ├── model/           UserAddress, AddressType
│   │   └── service/         UserAddressValidationService
│   ├── contact/
│   │   ├── model/           UserContact, ContactType, UserLink, LinkType
│   │   └── service/         UserContactValidationService, UserLinkValidationService
│   ├── interest/
│   │   ├── model/           Interest, UserInterest
│   │   └── service/         UserInterestValidationService
│   └── sharedkernel/
│       ├── model/           Id
│       ├── factory/         IdFactory  ← sinh KSUID
│       └── service/         IdService  ← chuyển đổi Id ↔ String
│
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/          *Entity (JPA entity)
│   │   ├── mapper/          Entity ↔ Domain mapper (MapStruct)
│   │   └── repository/      *RepositoryImpl, Jpa*Repository (Spring Data JPA)
│   ├── crypto/
│   │   └── password/        BCryptPasswordHasher
│   ├── grpc/                gRPC client stub (kết nối Trust Service)
│   ├── security/            JWT verifier, mTLS bootstrap, security filter
│   └── scheduler/           Background job
│
├── integration/
│   └── trust/               Tích hợp Trust Service (lấy key, quản lý cert)
│       ├── key/             VerificationKeyResolver, TrustKeyCleanup
│       └── ssl/             GrpcServerSslContextProvider
│
└── config/
    ├── usecase/             UseCaseConfig  ← wire use case và domain service
    ├── security/            PasswordHashConfig
    └── grpc/                Cấu hình gRPC server
```

---

## DDD Tactical Patterns

### Domain Service là Plain Java

Domain service trong `domain/*/service/` là **POJO thuần** — không có `@Service`, `@Component` hay phụ thuộc Spring.

```java
public class IdentifierNormalizer {
    public String normalize(String identifier, IdentifierType type) {
        // pure normalization logic — không Spring, không DB
    }
}
```

Chúng được khởi tạo trong `@Configuration` class (`UseCaseConfig`) hoặc inject qua constructor.

### Invariant Được Thực Thi Tại Construction

Domain invariant được thực thi trong constructor, không phải ở use case:

```java
public class User {
    public User(Id id, String username, String passwordHash, UserStatus status, ...) {
        this.username = Objects.requireNonNull(username);
        validate();  // ném exception ngay nếu vi phạm invariant
    }

    private void validate() {
        if (username.isBlank()) throw new IllegalArgumentException("username must not be blank");
    }
}
```

### Repository Interface ở Application Layer

Repository interface nằm tại `application/port/out/` — không phải trong `domain/`. Điều này tuân theo quy ước Hexagonal Architecture nơi port được định nghĩa ở ranh giới application:

```
application/port/out/user/UserRepository        ← interface
infrastructure/persistence/repository/
    JpaUserRepository                           ← Spring Data JPA
    UserRepositoryImpl  implements UserRepository ← adapter
```

Cách này cho phép tạo port chi tiết theo từng use case thay vì một god repository.

### State Change Trả Về Instance Mới

Domain model thực tế là immutable. State change trả về instance mới:

```java
// User là immutable — state change tạo ra object mới
User lockedUser  = user.lock(Instant.now());
User activeUser  = user.activate(Instant.now());
User deletedUser = user.delete(Instant.now());
```

---

## Quy tắc Đặt Tên

| Loại | Pattern | Ví dụ |
|---|---|---|
| JPA Entity | `*Entity` | `UserEntity`, `UserContactEntity` |
| Repository impl | `*RepositoryImpl` | `UserRepositoryImpl` |
| JPA Repository | `Jpa*Repository` | `JpaUserRepository` |
| Use Case | `*UseCase` | `LoginUseCase`, `RegisterUseCase` |
| Domain Service | (mô tả) | `IdentifierNormalizer`, `UserProfileValidationService` |
| gRPC API | `*GrpcApi` | `LoginGrpcApi`, `RegistrationGrpcApi` |
| Port (out) | mô tả | `UserRepository`, `PasswordHasher` |

---

## Tầng Mapper

Toàn bộ mapping được thực hiện bằng **MapStruct** annotation processor — không viết mapper thủ công:

```
Entity ↔ Domain model     (infrastructure/persistence/mapper)
Domain model ↔ DTO        (application/mapper)
DTO ↔ Protobuf            (api/grpc/mapper)
```

---

## Luồng Use Case

```
gRPC / REST Request
      ↓
  gRPC Handler / REST Controller
      → map proto message / HTTP body sang DTO
      ↓
  Use Case
      → validate input
      → normalize identifier (nếu cần)
      → resolve domain model qua repository
      → áp dụng domain logic / factory
      → save qua repository
      → trả về output DTO
      ↓
  Domain Service     → pure validation (không I/O)
      ↓
  Repository         → được resolve thành JPA implementation qua Spring DI
      ↓
  PostgreSQL
```

---

## Ranh Giới Bảo Mật

```
Bên ngoài (public internet)
      ↓ HTTPS + JWT
  REST API (api/rest/)     ← profile read/update (không nhạy cảm)

Nội bộ (service mesh)
      ↓ gRPC + mTLS
  gRPC API (api/grpc/)     ← credential verify, registration (nhạy cảm)
```

Không có thao tác credential nào được expose ra internet public. Client certificate mTLS xác định caller là Identity Service.
