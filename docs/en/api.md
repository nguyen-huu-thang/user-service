# API Reference

**English** | [Tiếng Việt](../vn/api.md)

---

## Overview

User Service exposes two groups of APIs:

| Group | Transport | Authentication | Purpose |
|---|---|---|---|
| **Internal gRPC** | gRPC + mTLS | Client certificate (Identity Service) | Credential verification, user registration |
| **External REST** | HTTPS | JWT Bearer token | Profile read and update |

Proto files are located in `src/main/proto/exposed/`:
- `internal/authentication/` — internal gRPC service definitions
- `external/` — external API definitions (if any)

---

## Internal gRPC APIs

All internal APIs require **mTLS**. The client certificate must be issued by the platform's Root CA (Trust Service). Only Identity Service is authorized to call these endpoints.

### LoginService

```protobuf
service LoginService {
  // Verify human credential and account state.
  // Called by Identity Service during the login flow.
  rpc VerifyCredential (VerifyCredentialRequest) returns (VerifyCredentialResponse);
}
```

**VerifyCredentialRequest**

```protobuf
message VerifyCredentialRequest {
  string identifier      = 1;  // normalized identifier (email / phone / username)
  IdentifierType identifier_type = 2;
  string credential      = 3;  // raw credential — NEVER log this field
  CredentialType credential_type = 4;
  string user_agent      = 5;  // device / client metadata
  string shard_id        = 6;  // target shard routing hint
}
```

**VerifyCredentialResponse**

```protobuf
message VerifyCredentialResponse {
  bool success           = 1;  // true if credential valid and account allowed
  VerifiedIdentityDto identity = 2;  // populated only when success = true
  string failure_reason  = 3;  // IDENTITY_NOT_FOUND / INVALID_CREDENTIAL / ACCOUNT_LOCKED / ACCOUNT_DELETED
  bool locked            = 4;
  bool disabled          = 5;
  bool requires_mfa      = 6;
}
```

**Failure Reasons**

| Code | Meaning |
|---|---|
| `IDENTITY_NOT_FOUND` | No user found with the given identifier on this shard |
| `INVALID_CREDENTIAL` | User found but password does not match |
| `ACCOUNT_LOCKED` | Account exists and credential matches, but account is locked |
| `ACCOUNT_DELETED` | Account has been soft-deleted |

---

### RegistrationService

```protobuf
service RegistrationService {
  // Create a new human account.
  // Called by Identity Service during the registration flow.
  rpc RegisterUser (RegisterUserRequest) returns (RegisterUserResponse);
}
```

**RegisterUserRequest**

```protobuf
message RegisterUserRequest {
  string identifier      = 1;  // normalized identifier
  IdentifierType identifier_type = 2;
  string credential      = 3;  // raw credential — NEVER log this field
  CredentialType credential_type = 4;
  string user_agent      = 5;
  string shard_id        = 6;  // allocated target shard
}
```

**RegisterUserResponse**

```protobuf
message RegisterUserResponse {
  string identity_id = 1;  // new user ID (KSUID, same as user ID)
  string shard_id    = 2;  // assigned shard
  int64  created_at  = 3;  // epoch millis
}
```

---

## Shared Types

### VerifiedIdentityDto

Returned on successful credential verification. Contains the minimum information Identity Service needs to issue a JWT.

```protobuf
message VerifiedIdentityDto {
  string identity_id  = 1;  // user ID (KSUID)
  string subject_type = 2;  // always "HUMAN" for User Service
  string shard_id     = 3;  // shard where the user lives
  string service_id   = 4;  // always "user-service"
  string tenant_id    = 5;  // optional — for multi-tenant applications
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

## External REST APIs

External REST endpoints are authenticated with a **JWT Bearer token** issued by Identity Service. They are for non-sensitive profile operations only.

### Profile Endpoints

```
GET    /api/v1/profile/{userId}           ← get user profile
PUT    /api/v1/profile/{userId}           ← update user profile
GET    /api/v1/profile/{userId}/contacts  ← get user contacts
POST   /api/v1/profile/{userId}/contacts  ← add contact
GET    /api/v1/profile/{userId}/addresses ← get addresses
```

Profile endpoints never expose credential data (password hash, oauth tokens, etc.).

---

## API Usage Rules

### Credential API (Internal)

- **Never call from application services** — only Identity Service may call `LoginService` and `RegistrationService`
- The `credential` field in requests must **never be logged** at any layer
- Always pass a normalized `identifier` — User Service normalizes defensively, but the caller should normalize first
- Include a valid `shard_id` — routing to the wrong shard will result in `IDENTITY_NOT_FOUND`

### Profile API (External)

- Authenticate all requests with a valid JWT
- User Service verifies the JWT locally using cached public keys from Trust Service
- Profile endpoints are safe to call from application services and frontend

### Error Handling

| gRPC Status | Meaning |
|---|---|
| `OK` | Request processed — check `success` field in response |
| `INVALID_ARGUMENT` | Missing or malformed request fields |
| `UNAUTHENTICATED` | mTLS cert missing or invalid |
| `PERMISSION_DENIED` | mTLS cert valid but caller not authorized |
| `INTERNAL` | Unexpected server error |

Note: a failed credential verification returns `OK` status with `success = false` and a `failure_reason` — it is not an error at the gRPC level.
