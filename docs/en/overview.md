# Overview

**English** | [Tiếng Việt](../vn/overview.md)

---

## What is User Service?

User Service is the **Human Identity Domain Service** of the Xime Base Platform.

Its role is to be the authoritative source of truth for everything that defines a human account on the platform — credentials that prove who a user is, account state that determines whether they may act, and profile data that describes them. No other service owns or duplicates this data.

```
Client (mobile, web, third-party)
      ↓ login / register
Identity Service   ← authentication gateway, issues JWT
      ↓ VerifyCredential / RegisterUser  (gRPC + mTLS, internal only)
User Service       ← credential owner, account state authority, profile store
      ↓
PostgreSQL  (per-shard database)
```

---

## Position in Base Platform

The Xime Base Platform is divided into two layers:

### Base Platform (core services)

Generic, reusable infrastructure services built once and shared across all applications:

| Service | Role |
|---|---|
| `trust-service` | Trust infrastructure — CA, mTLS, JWT signing keys |
| `identity-service` | Authentication — JWT issuance, refresh tokens, session |
| `user-service` | **Human Identity Domain — credentials, account state, profile** |
| `data-service` | Data infrastructure — object storage, permission |
| `notification-service` | Notification delivery |
| `payment-service` | Payment processing |

### Application Layer (business services)

Application-specific logic built on top of Base Platform:

- **Social Network**: post-service, comment-service, media-service
- **Ecommerce**: product-service, order-service
- **SaaS / AI**: workspace-service, dataset-service, ai-agent-service

Application services reference user identity but never duplicate it. They call User Service (through Identity Service or directly, with appropriate permissions) to resolve human identity data.

---

## Two Domains in One Service

User Service manages two clearly separated domains that share a user ID but differ in sensitivity and access patterns:

### A. Authentication Domain (sensitive)

Everything needed to authenticate a user and determine whether login is allowed:

```
User Service (auth domain)
  ├── credential: passwordHash, oauth mapping, passkey
  ├── account state: ACTIVE / LOCKED / DISABLED / BANNED / PENDING_VERIFICATION
  └── authentication metadata: token version, last login, device trust
```

- Exposed only via **internal gRPC + mTLS** (no public REST)
- Called by Identity Service during the login and registration flow
- Highly sensitive — never logged, never exposed to application layer directly

### B. Profile Domain (non-sensitive)

Everything that describes a user visually and socially:

```
User Service (profile domain)
  ├── profile: full name, display name, avatar, bio, date of birth, gender
  ├── contact: email (verified/primary), phone (verified/primary)
  ├── address: shipping/billing addresses
  ├── links: GitHub, Twitter, LinkedIn, website
  └── interests: user-selected interest tags
```

- Exposed via **external REST API** (authenticated via JWT)
- Called by application services to render user information
- Lower sensitivity — safe to cache and expose to frontend

---

## Design Philosophy

| Question | Answer |
|---|---|
| What is User Service? | Human Identity Domain Service — credential owner, account authority |
| What does it own? | Credentials, account state, user profile |
| Does it issue JWTs? | No — Identity Service is the authentication gateway |
| Does it manage sessions? | No — session lifecycle belongs to Identity Service |
| Is credential API public? | No — internal gRPC + mTLS only |
| Where does profile live? | User Service — served via external REST API |
| Can it be down? | JWT verify still works; login and registration will fail until recovered |

---

## Relationship with Identity Service

User Service and Identity Service are **distinct services with a clear contract**:

```
Identity Service responsibility:
  - receive login/register request from client
  - normalize identifier
  - call User Service to verify credential and account state
  - issue JWT if verification passes
  - manage refresh token lifecycle

User Service responsibility:
  - verify that the credential matches what is stored
  - check whether the account is allowed to log in
  - return verified identity data (user ID, shard, subject type)
  - own and update credential data
```

User Service never generates or validates JWTs. Identity Service never stores passwords or account state.

---

## What User Service Is NOT

- Not an authentication gateway — it does not handle client login requests directly
- Not a token issuer — it has no knowledge of JWT structure
- Not a session manager — it does not track active sessions
- Not a business domain service — it contains no social, commerce, or application logic
- Not a global directory — each instance manages one shard only
