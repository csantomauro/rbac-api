# RBAC API

A small CMS-style REST API demonstrating role-based access control (RBAC) with JWT authentication, built with Java, Spring Boot, and PostgreSQL. Includes method-level authorization, ownership-based rules, and caching with cache eviction on role changes.

**Stack:** Java 17+ · Spring Boot · PostgreSQL · Spring Data JPA · Spring Security (JWT + `@PreAuthorize`) · Spring Cache · JUnit 5 / Mockito · Docker

## Getting started

```bash
git clone git@github.com:yourusername/rbac-api.git
cd rbac-api
docker compose up -d
./mvnw spring-boot:run
```

API available at `http://localhost:8080`. Run tests with `./mvnw test`.

New users register with the `VIEWER` role by default. To create the first `ADMIN`, promote a user directly in the database:
```bash
docker exec -it rbac-postgres psql -U rbacuser -d rbacdb
UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
```

## Roles

| Role | Permissions |
|---|---|
| `VIEWER` | Read articles |
| `EDITOR` | Read + create articles, edit their own articles |
| `ADMIN` | All of the above, plus edit any article and change user roles |

## Usage examples

**Register and log in**
```bash
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" \
  -d '{"username": "mario", "password": "password123"}'

curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" \
  -d '{"username": "mario", "password": "password123"}'
```
Both return a JWT: `{ "token": "..." }`. Send it as `Authorization: Bearer <token>` on subsequent requests.

**Create an article** (requires `EDITOR` or `ADMIN`)
```bash
curl -X POST http://localhost:8080/articles \
  -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"title": "Hello World", "content": "My first article"}'
```

**Update an article** (author or `ADMIN` only)
```bash
curl -X PUT http://localhost:8080/articles/1 \
  -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"title": "Updated title", "content": "Updated content"}'
```

**Change a user's role** (`ADMIN` only)
```bash
curl -X PUT http://localhost:8080/users/2/role \
  -H "Authorization: Bearer <admin_token>" -H "Content-Type: application/json" \
  -d '{"role": "EDITOR"}'
```

| Method | Endpoint | Access |
|---|---|---|
| POST | `/auth/register` | Public |
| POST | `/auth/login` | Public |
| GET | `/articles` | Any authenticated user |
| POST | `/articles` | `EDITOR`, `ADMIN` |
| PUT | `/articles/{id}` | Author or `ADMIN` |
| PUT | `/users/{id}/role` | `ADMIN` |

## Design notes

- **Roles are re-read from the database on every request** rather than embedded in the JWT, so a role change takes effect immediately instead of only at the next login.
- **Two layers of authorization**: `.authorizeHttpRequests()` handles coarse, path-level rules (authenticated vs public), while `@PreAuthorize` handles role checks per endpoint, and ownership checks (author vs `ADMIN`) live in the service layer, where the actual data is available.
- **Caching**: user lookups are cached to avoid a database hit on every authenticated request, with `@CacheEvict` triggered on role updates, since a stale cached role would otherwise persist until the user logs in again.