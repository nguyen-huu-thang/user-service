# Identity System

**English** | [Tiếng Việt](../vn/identity-system.md)

---

## Overview

The identity system in User Service has three components that work together to answer the question: *"Can this user log in, and who are they?"*

| Component | Responsibility |
|---|---|
| **Identifier Normalization** | Ensure all identifiers are canonical before storage or lookup |
| **Credential Verification** | Confirm the user knows their password (or equivalent) |
| **Account State** | Determine whether the account is permitted to log in |

---

## Identifier Normalization

### Why It Matters

Users may register with `TEST@MAIL.COM` and try to log in with `test@mail.com`. Without normalization, these are treated as different identifiers — the login fails, or a duplicate account is created.

Normalization rules:

| Identifier Type | Rule | Example |
|---|---|---|
| Email | Lowercase entire address | `TEST@Mail.com` → `test@mail.com` |
| Phone | Strip formatting, use E.164 without `+` | `+84 123 456 789` → `84123456789` |
| Username | Lowercase + trim + unicode normalization | `  User_Ñame  ` → `user_ñame` |

### Where Normalization Happens

`IdentifierNormalizer` is a **domain service** — plain Java, no Spring dependencies:

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

Normalization is applied:
- In `LoginUseCase` before resolving the user
- In `RegisterUseCase` before checking availability and saving
- At every entry point — even if the caller (Identity Service) has already normalized

The double-normalization at User Service is defensive: it guards against bugs in callers and cross-service inconsistencies.

### Normalization is NOT Optional

An identifier stored without normalization is a data integrity violation. The `IdentifierNormalizer` must be called before any save or query operation involving an identifier. This rule is documented in `architecture-rules.md` and enforced by code review.

---

## Credential System

### Current State (Stage 1)

In the current implementation, the password hash lives directly inside the `User` aggregate:

```
users table:
  id            BYTEA      ← KSUID
  username      VARCHAR
  password_hash TEXT       ← BCrypt hash (temporary location)
  status        VARCHAR
  created_at    TIMESTAMP
  updated_at    TIMESTAMP
```

This is a known architectural compromise. It works correctly but does not scale to multiple credential types (OAuth, passkey, API key, MFA) without coupling them into `User`.

### Future Design (Stage 2)

The planned `Credential` aggregate separates credential storage from account state:

```
users table:              ← account state only
  id, username, status, created_at, updated_at

user_credentials table:   ← one row per credential type
  id            BYTEA
  user_id       BYTEA  → users.id
  type          VARCHAR  ← PASSWORD / OAUTH_GOOGLE / OAUTH_GITHUB / PASSKEY / API_KEY / CERTIFICATE
  value         TEXT     ← hash, token, public key — depends on type
  metadata      JSONB    ← provider ID, device info, etc.
  created_at    TIMESTAMP
  updated_at    TIMESTAMP
```

This allows one user to have multiple credential types without schema changes. Migration from Stage 1 to Stage 2 moves `password_hash` from `users` to `user_credentials`.

### Supported Credential Types

| Type | Description | Status |
|---|---|---|
| `PASSWORD` | BCrypt or Argon2id hash | Implemented |
| `OAUTH_GOOGLE` | Google OAuth provider mapping | Planned |
| `OAUTH_GITHUB` | GitHub OAuth provider mapping | Planned |
| `PASSKEY` | FIDO2 WebAuthn public key | Planned |
| `API_KEY` | Machine-to-machine API key | Planned |
| `CERTIFICATE` | Client certificate for service accounts | Planned |

### Password Hashing

Password hashing is delegated to `PasswordHasher` port:

```java
public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String hash);
}
```

The current implementation (`BCryptPasswordHasher`) uses BCrypt with default cost factor. The abstraction allows switching to Argon2id without changing use cases.

---

## Account State

### State Model

```
ACTIVE               ← can log in, normal operation
LOCKED               ← temporarily prevented from logging in (admin action or security trigger)
DISABLED             ← account self-disabled or administratively disabled
BANNED               ← permanent restriction (policy violation)
PENDING_VERIFICATION ← registered but email/phone not yet verified
DELETED              ← soft-deleted, cannot recover
```

### State Transitions

```
PENDING_VERIFICATION → ACTIVE      (verification complete)
ACTIVE              → LOCKED       (security trigger or admin)
ACTIVE              → DISABLED     (self-service or admin)
ACTIVE              → BANNED       (policy enforcement)
ACTIVE/LOCKED/DISABLED → DELETED   (data deletion request)
LOCKED              → ACTIVE       (admin unlock)
DISABLED            → ACTIVE       (re-enable)
```

State changes are modeled as immutable operations on `User`:

```java
User locked  = user.lock(now);    // returns new User with LOCKED status
User active  = user.activate(now); // returns new User with ACTIVE status
User deleted = user.delete(now);   // returns new User with DELETED status
```

A deleted user cannot be re-activated. No state transition from `DELETED` is allowed.

### State Checks in Login Flow

```
credential verified?
      ↓ yes
account DELETED?   → return failure: ACCOUNT_DELETED
      ↓ no
account LOCKED?    → return failure: ACCOUNT_LOCKED
      ↓ no
account ACTIVE?    → return success with VerifiedIdentity
```

Disabled and banned states follow the same pattern — each returns a specific failure reason so Identity Service can provide an appropriate user-facing message.

---

## Identifier Routing and Sharding

### Immutable Shard Assignment

When a user is registered, they are assigned to a shard. This assignment is permanent. The user's data (auth, profile, contacts, addresses) all live on the same shard.

```
Registration time:
  Identity Service allocates shard → user.shard_id = "U00000"
  User Service shard "U00000" creates the user record

Query time:
  caller must know the shard_id to route directly to the correct User Service instance
  or → Search Service resolves identifier → shard_id → direct routing
```

### Finding a User by Identifier

User Service does not maintain a global lookup table. Finding a user from an email or phone number requires:

```
email/phone → Search Service → resolves (shard_id, user_id) → User Service[shard_id]
```

User Service itself only handles lookups within its own shard. Cross-shard queries go through Search Service.

### Shard ID in Login Flow

The `VerifyCredential` request includes `shard_id`:

```protobuf
message VerifyCredentialRequest {
  string identifier  = 1;
  IdentifierType identifier_type = 2;
  string credential  = 3;
  CredentialType credential_type = 4;
  string user_agent  = 5;
  string shard_id    = 6;  ← routing hint: which User Service instance to call
}
```

Identity Service resolves the shard before calling User Service, ensuring the request goes to the correct instance.
