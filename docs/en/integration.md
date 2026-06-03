# Integration

**English** | [Tiếng Việt](../vn/integration.md)

---

## Overview

User Service integrates with two other Base Platform services:

| Integration | Direction | When |
|---|---|---|
| Credential verification | Identity Service → User Service | Every login attempt |
| User registration | Identity Service → User Service | Every new account creation |
| JWT public key fetch | User Service → Trust Service | Startup + periodic refresh |
| mTLS certificate | User Service → Trust Service | Bootstrap + cert rotation |

---

## Identity Service Integration

Identity Service is the **only** service authorized to call User Service's internal gRPC APIs. All credential operations go through this path.

### Login Flow

```
Client sends login request
      ↓
Identity Service
  1. receives login request (email/phone/username + password)
  2. normalizes identifier
  3. resolves shard_id via Search Service
  4. calls User Service[shard_id].VerifyCredential (gRPC + mTLS)
      ↓
User Service
  5. normalizes identifier (defensive)
  6. resolves user from local shard DB
  7. verifies password against stored hash
  8. checks account state (ACTIVE / LOCKED / DELETED)
  9. returns VerifyCredentialResponse
      ↓
Identity Service
  10. on success: issues JWT with {user_id, shard_id, subject_type}
  11. on failure: maps failure_reason to appropriate HTTP response
```

User Service never knows about JWT structure. Identity Service never stores passwords.

### Registration Flow

```
Client sends register request
      ↓
Identity Service
  1. receives registration request
  2. normalizes identifier
  3. allocates shard_id (routing decision)
  4. calls User Service[shard_id].RegisterUser (gRPC + mTLS)
      ↓
User Service
  5. normalizes identifier (defensive)
  6. checks identifier availability on local shard
  7. hashes password
  8. creates User domain model + UserContact
  9. persists to database
  10. returns RegisterUserResponse {identity_id, shard_id, created_at}
      ↓
Identity Service
  11. issues initial JWT
  12. creates refresh token
  13. returns token pair to client
```

---

## Trust Service Integration

User Service integrates with Trust Service for two purposes: verifying JWTs locally, and participating in mTLS as a service.

### JWT Public Key Fetch

User Service verifies inbound JWTs (for external REST endpoints) locally using public keys fetched from Trust Service:

```
User Service startup
      ↓
  Fetch public keys from Trust Service (GetPublicKeys)
      ↓
  Cache key set in memory (KeyContext)
      ↓
  Inbound REST request with JWT Bearer token
      ↓
  Extract kid from JWT header
      ↓
  Find key in local cache by kid
      ↓
  Verify JWT signature locally — NO call to Trust Service
```

Public key refresh runs on a background schedule. Trust Service downtime does not affect JWT verification as long as the cache is warm.

### mTLS Certificate Bootstrap

User Service participates in mTLS as both a client (calling other services) and a server (receiving calls from Identity Service):

```
First deployment
  1. Platform admin issues initial cert via Trust Service admin API
  2. User Service loads cert + private key from bootstrap file
  3. All inbound gRPC connections require client cert from same Root CA

Cert rotation (every ~100 days)
  1. User Service detects cert approaching rotation threshold
  2. Generates new key pair locally
  3. Calls Trust Service.RotateCertificate with refresh token + new public key
  4. Receives new cert + new refresh token
  5. Reloads TLS configuration with new cert
```

Bootstrap configuration in `application.yml`:

```yaml
user:
  bootstrap:
    service-id: user-service
    shard-id: U00000
    path: ./runtime/security/bootstrap.txt
```

The `bootstrap.txt` file contains the initial certificate and refresh token issued by Trust Service during deployment.

---

## Application Service Integration

Application services (social, ecommerce, SaaS) interact with User Service only for **profile data**, never for credential data.

```
Application Service (e.g., post-service)
  ↓ needs to display post author name + avatar
  GET /api/v1/profile/{userId}
  ↓ (HTTPS + JWT)
User Service External REST API
  ↓ returns: display_name, avatar_url, bio
```

Application services must not call internal gRPC endpoints. They should cache profile data that does not change frequently (display name, avatar URL) to reduce load on User Service.

---

## Search Service Integration (Planned)

User Service does not currently maintain a global identifier index. Finding a user by email or phone across shards requires Search Service:

```
identifier (email / phone)
      ↓
Search Service          ← maintains global identifier → shard_id mapping
      ↓
resolves (shard_id, user_id)
      ↓
caller routes to User Service[shard_id]
```

When a user registers, User Service will eventually publish an event that Search Service consumes to update its index. Until then, identifier routing is handled by Identity Service's internal mapping.

---

## Integration Summary

```
Trust Service
  ↓ public keys (startup + periodic)
  ↓ mTLS cert (bootstrap + rotation)

User Service
  ↑ VerifyCredential (every login)
  ↑ RegisterUser (every registration)
Identity Service

User Service
  ↓ profile data (on demand)
Application Services (post-service, order-service, etc.)
```

User Service is **in the critical path for login and registration** — its availability directly impacts user-facing authentication. It is not in the critical path for JWT verification (which is always local).
