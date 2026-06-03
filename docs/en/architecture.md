# Architecture

**English** | [Tiếng Việt](../vn/architecture.md)

---

## Layered Overview

User Service follows **Hexagonal Architecture** (Ports and Adapters) with DDD tactical patterns, built on Spring Boot 4 / Java 25.

```
External Clients (gRPC internal, REST external)
        ↓
   Adapter Layer (api/)           ← receives requests, maps to use case inputs
        ↓
 Application Layer (application/) ← use cases, services, orchestration
        ↓
   Domain Layer (domain/)         ← pure business models, no framework deps
        ↑
Infrastructure Layer (infrastructure/) ← JPA, crypto, gRPC client, security
```

The domain layer has no knowledge of Spring, JPA, or gRPC. The infrastructure layer has no knowledge of use case logic. Dependencies always point inward toward the domain.

---

## Directory Structure

```
src/main/java/vn/xime/user/
│
├── api/                                    ← adapter layer
│   ├── grpc/
│   │   ├── identity/                       ← internal gRPC handlers
│   │   │   ├── LoginGrpcApi               ← handles VerifyCredential
│   │   │   └── RegistrationGrpcApi        ← handles RegisterUser
│   │   └── mapper/                        ← DTO ↔ Protobuf mappers (MapStruct)
│   │       ├── LoginGrpcMapper
│   │       └── RegistrationGrpcMapper
│   └── rest/
│       └── external/profile/              ← external REST handlers (JWT-authenticated)
│
├── application/
│   ├── usecase/                           ← orchestrate: validate → domain → repo → return
│   │   └── identity/
│   │       ├── LoginUseCase               ← verify credential, check account state
│   │       ├── RegisterUseCase            ← create user + credential + contact
│   │       └── RecoveryUseCase            ← account recovery flow
│   ├── service/                           ← application services (Spring beans)
│   │   └── identity/
│   │       ├── ResolveUserByIdentifierService  ← find user by email/phone/username
│   │       └── CheckIdentifierAvailabilityService
│   ├── port/                              ← inbound / outbound port interfaces
│   │   └── out/
│   │       ├── user/        UserRepository, UserUsernameHistoryRepository
│   │       ├── profile/     UserProfileRepository
│   │       ├── contact/     UserContactRepository
│   │       ├── address/     UserAddressRepository, UserLinkRepository
│   │       ├── interest/    InterestRepository, UserInterestRepository
│   │       ├── crypto/      PasswordHasher
│   │       ├── security/    JwtTokenVerifier
│   │       └── integration/ ResolveVerificationKey
│   ├── dto/                               ← input/output DTOs
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
│   │   └── model/           UserProfile, Gender
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
│       ├── factory/         IdFactory  ← KSUID generation
│       └── service/         IdService  ← Id ↔ String conversion
│
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/          *Entity (JPA entities)
│   │   ├── mapper/          Entity ↔ Domain mappers (MapStruct)
│   │   └── repository/      *RepositoryImpl, Jpa*Repository (Spring Data JPA)
│   ├── crypto/
│   │   └── password/        BCryptPasswordHasher
│   ├── grpc/                gRPC client stubs (Trust Service connection)
│   ├── security/            JWT verifier, mTLS bootstrap, security filters
│   └── scheduler/           Background jobs
│
├── integration/
│   └── trust/               Trust Service integration (key fetch, cert management)
│       ├── key/             VerificationKeyResolver, TrustKeyCleanup
│       └── ssl/             GrpcServerSslContextProvider
│
└── config/
    ├── usecase/             UseCaseConfig  ← wire use cases and domain services
    ├── security/            PasswordHashConfig
    └── grpc/                gRPC server configuration
```

---

## DDD Tactical Patterns

### Domain Services are Plain Java

Domain services in `domain/*/service/` are **pure POJOs** — no `@Service`, no `@Component`, no Spring dependencies.

```java
public class IdentifierNormalizer {
    public String normalize(String identifier, IdentifierType type) {
        // pure normalization logic — no Spring, no DB
    }
}
```

They are instantiated in a `@Configuration` class (`UseCaseConfig`) or injected as constructor parameters.

### Invariants Enforced at Construction

Domain invariants are enforced inside constructors, not in use cases:

```java
public class User {
    public User(Id id, String username, String passwordHash, UserStatus status, ...) {
        this.username = Objects.requireNonNull(username);
        validate();  // throws immediately if invariant is violated
    }

    private void validate() {
        if (username.isBlank()) throw new IllegalArgumentException("username must not be blank");
    }
}
```

### Repository Interfaces in Application Layer

Repository interfaces live in `application/port/out/` — not in `domain/`. This follows the Hexagonal Architecture convention where ports are defined at the application boundary:

```
application/port/out/user/UserRepository        ← interface
infrastructure/persistence/repository/
    JpaUserRepository                           ← Spring Data JPA
    UserRepositoryImpl  implements UserRepository ← adapter
```

This allows fine-grained ports per use case instead of a single god repository.

### State Changes Return New Instances

Domain models are effectively immutable. State changes return new instances:

```java
// User is immutable — state changes produce new objects
User lockedUser = user.lock(Instant.now());
User activeUser = user.activate(Instant.now());
```

---

## Naming Conventions

| Type | Pattern | Example |
|---|---|---|
| JPA Entity | `*Entity` | `UserEntity`, `UserContactEntity` |
| Repository impl | `*RepositoryImpl` | `UserRepositoryImpl` |
| JPA Repository | `Jpa*Repository` | `JpaUserRepository` |
| Use Case | `*UseCase` | `LoginUseCase`, `RegisterUseCase` |
| Domain Service | (descriptive) | `IdentifierNormalizer`, `UserProfileValidationService` |
| gRPC API | `*GrpcApi` | `LoginGrpcApi`, `RegistrationGrpcApi` |
| Port (out) | descriptive | `UserRepository`, `PasswordHasher` |

---

## Mapper Layer

All mapping is done with **MapStruct** annotation processor — no manual mappers:

```
Entity ↔ Domain model     (infrastructure/persistence/mapper)
Domain model ↔ DTO        (application/mapper)
DTO ↔ Protobuf            (api/grpc/mapper)
```

---

## Use Case Flow

```
gRPC / REST Request
      ↓
  gRPC Handler / REST Controller
      → maps proto message / HTTP body to DTO
      ↓
  Use Case
      → validate input
      → normalize identifier (if applicable)
      → resolve domain model via repository
      → apply domain logic / factory
      → save via repository
      → return output DTO
      ↓
  Domain Service     → pure validation (no I/O)
      ↓
  Repository         → resolved to JPA implementation via Spring DI
      ↓
  PostgreSQL
```

---

## Security Boundary

```
External (public internet)
      ↓ HTTPS + JWT
  REST API (api/rest/)     ← profile read/update (non-sensitive)

Internal (service mesh)
      ↓ gRPC + mTLS
  gRPC API (api/grpc/)     ← credential verify, registration (sensitive)
```

No credential operation is ever exposed to the public internet. The mTLS client certificate identifies the caller as Identity Service.
