# User Service

**English** | [Tiếng Việt](README-vn.md)

> Human Identity Domain Service for the Xime Base Platform — managing user accounts, credentials, account state, and user profile data.

---

User Service is the **human identity layer** of the Xime Base Platform. It is the source of truth for everything that belongs to a human account — credentials, account state, and profile information. It does not issue tokens or manage sessions; that is Identity Service's responsibility.

```
Client
  ↓ login / register
Identity Service     ← authentication gateway, issues JWT
  ↓ VerifyCredential / RegisterUser (gRPC + mTLS)
User Service         ← credential owner, account state, profile
  ↓
PostgreSQL
```

---

## What User Service Does

**Credential Management**
- Own and verify user credentials (password, OAuth, passkey, API key)
- Hash passwords at rest (BCrypt / Argon2id)
- Validate credential correctness on login

**Account State**
- Maintain account lifecycle: `ACTIVE`, `LOCKED`, `DISABLED`, `BANNED`, `PENDING_VERIFICATION`
- Enforce state checks before returning a successful verification
- Serve as the authority on whether a human account may log in

**User Profile**
- Store and serve user profile data: name, avatar, bio, date of birth, locale
- Manage contact information (email, phone) with verification flags
- Manage addresses, social links, and interests

## What User Service Does NOT Do

- Does not issue JWT tokens — that is Identity Service
- Does not manage sessions or refresh tokens
- Does not handle platform authorization or RBAC
- Does not contain social graph, posts, orders, or any business domain data
- Does not expose credential APIs publicly — internal gRPC + mTLS only

---

## Key Design Decisions

### Identifier Normalization

All identifiers are normalized before any storage or lookup:

```
Email:    TEST@Mail.com     → test@mail.com
Phone:    +84 123 456 789  → 84123456789
Username: → lowercase + trim + unicode normalization
```

Not normalizing leads to duplicate identifiers, routing errors, and shard mismatches.

### Immutable Shard Placement

A user is assigned to a shard at registration time and never moves. All auth data, profile, and contacts live on the same shard to maximize locality and minimize cross-shard calls.

### Credential API — Internal Only

Credential verification and registration are exposed **only** via gRPC + mTLS. There is no public REST endpoint for these operations. The caller (Identity Service) is authenticated via client certificate.

### PasswordHash in User (Temporary)

Currently `passwordHash` lives directly in the `User` aggregate. This is intentional for the current stage. The design calls for a separate `Credential` aggregate in a future iteration, which will support multiple credential types per user cleanly.

---

## Quick Start

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

REST: `8083` | gRPC: `9092`

Requires PostgreSQL at `localhost:5432/user_service` and Trust Service (for mTLS bootstrap).

---

## Architecture

User Service follows **Hexagonal Architecture** with DDD tactical patterns, built on Spring Boot 4 / Java 25:

```
src/main/java/
├── api/            ← Input adapters: gRPC (internal), REST (external)
├── application/    ← Use cases, services, ports, DTOs, mappers
├── domain/         ← Pure business models, factories, domain services
└── infrastructure/ ← JPA persistence, crypto, gRPC clients, security
```

Repository interfaces live in `application/port/out/` (not in `domain/`). Domain services are plain Java with no Spring annotations.

---

## Documentation

| Document | Description |
|---|---|
| [Overview](docs/en/overview.md) | Role, boundaries, position in Base Platform |
| [Architecture](docs/en/architecture.md) | Layer structure, DDD patterns, directory layout |
| [Identity System](docs/en/identity-system.md) | Account state, credential model, identifier normalization |
| [API Reference](docs/en/api.md) | gRPC internal API definitions and usage rules |
| [Integration](docs/en/integration.md) | How Identity Service integrates with User Service |

---

## Base Platform Services

| Service | Role |
|---|---|
| `trust-service` | Trust infrastructure — CA, mTLS, JWT signing keys |
| `identity-service` | Authentication infrastructure — JWT, refresh tokens |
| `user-service` | **Human Identity Domain Service** |
| `data-service` | Data infrastructure — object storage, permission |
| `notification-service` | Notification delivery |
| `payment-service` | Payment processing |

---

## Project Status

User Service is in **active development**. The credential verification (login) and user registration flows are implemented. User profile CRUD, contact management, and mTLS bootstrap are operational.

---

## License

MIT
