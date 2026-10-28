# AuthenticationService

AuthenticationService registers users, issues signed JWTs, and verifies JWTs.
Usernames are stored in the `name` field; registration also requires a unique
email address. Passwords are BCrypt-hashed before they are stored.

The service uses the existing MySQL database `csb` with the requested local
username (`root`), listens on port `9898`, and registers with Eureka. Set the
database password in the `DB_PASSWORD` environment variable before starting;
for the requested local password, run `$env:DB_PASSWORD = 'root'` in PowerShell.
It creates or updates the `user_credentials` table using JPA.

## Endpoints

- `POST /auth/register` — JSON fields: `name`, `email`, `password`
- `POST /auth/token` — JSON fields: `username`, `password` (username is the registered name)
- `POST /auth/verify` — JSON field: `token`
- `GET /auth/validate?token=...` — verification-compatible endpoint

The API Gateway exposes these paths at `http://localhost:8765`. Registration,
token issuance, and verification are public; customer and savings API paths
require `Authorization: Bearer <token>`.

The default JWT signing key is for local development only. Set `JWT_SECRET` to
a private value of at least 32 bytes before deploying outside a local setup.

Start DiscoveryService, then AuthenticationService, CustomerService,
AccountService, and ApiGateway. Build this module with:

```powershell
.\mvnw.cmd -DskipTests package
```
