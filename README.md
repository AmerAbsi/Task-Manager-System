# Task Manager — User and Task Management System

A full-stack task management system with role-based access control, threaded comments, an audit trail, and real-time notifications.

Built with **Spring Boot 4.1** and **Angular 21**, backed by **MySQL 8**.

---

## Table of contents

- [Tech stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Database setup](#database-setup)
- [Running the backend](#running-the-backend)
- [Running the frontend](#running-the-frontend)
- [Default credentials](#default-credentials)
- [Project structure](#project-structure)
- [API overview](#api-overview)
- [Design decisions](#design-decisions)
- [Known limitations](#known-limitations)

---

## Tech stack

### Backend

| Component | Version |
|---|---|
| Java | 17 |
| Spring Boot | 4.1.0 |
| Spring Security | 7.1.0 |
| Hibernate ORM | 7.4.1 |
| JJWT | 0.13.0 |
| MySQL Connector/J | 9.7.0 |
| Build tool | Maven |

### Frontend

| Component | Version |
|---|---|
| Angular | 21.2 |
| TypeScript | 5.9 |
| Bootstrap | 5 |
| Bootstrap Icons | latest |
| @stomp/stompjs | latest |
| Node.js | 20.19+ required |

---

## Prerequisites

- **JDK 17** or later
- **Maven 3.9+** (or use the bundled `mvnw` wrapper)
- **Node.js 20.19+** and npm
- **MySQL Server 8.0+** running on port 3306

---

## Database setup

### Database choice: MySQL 8.0

This project uses **MySQL 8.0**, chosen over the embedded H2 option for a more production-like environment (real foreign key enforcement, native enum/integer column types, and behaviour that matches a deployed setup).

### Setup steps

**1. Ensure MySQL 8 is running on `localhost:3306`.**

You do **not** need to create the schema manually. The JDBC URL includes `createDatabaseIfNotExist=true`, so `taskmanager_db` is created automatically on first startup.

**2. Set your MySQL root password as an environment variable.**

Windows:
```
setx DB_PASSWORD "your_password"
```
Then fully close and reopen your IDE or terminal — `setx` only affects newly launched processes.

macOS / Linux:
```
export DB_PASSWORD="your_password"
```

Optionally set `DB_USERNAME` if you use an account other than `root`.

**3. Optionally set a JWT signing secret.**

```
setx JWT_SECRET "<base64-encoded-secret-at-least-32-bytes>"
```

A development default is provided in `application.yaml`. **Replace it before any real deployment.**

### Connection parameters explained

The JDBC URL contains three parameters worth noting:

| Parameter | Reason |
|---|---|
| `createDatabaseIfNotExist=true` | Creates the schema on first run so no manual SQL is required |
| `allowPublicKeyRetrieval=true` | MySQL 8's `caching_sha2_password` plugin needs the server's public key on first connect; the driver won't fetch it over an unencrypted connection without this flag |
| `serverTimezone=UTC` | Prevents timestamp drift between the JVM and the database |

### If you have XAMPP or Laragon installed

Those bundle **MariaDB**, which occupies port 3306 by default and will be used instead of MySQL. Symptoms are a reported database version of `5.5.5` and a Hibernate `HHH000511` dialect warning.

To check which server is actually running:
```sql
SELECT VERSION();
```

To switch (Windows, as Administrator):
```
net stop mysql
sc config mysql start= demand
net start MySQL80
```

---

## Running the backend

From the `backend/` directory:

```
mvn clean install
mvn spring-boot:run
```

Or run `TaskManagerApplication.java` directly from your IDE.

The API starts on **`http://localhost:8081/api`**.

Successful startup logs:
```
Database version: 8.0.x
Seeded default admin: admin / Admin123!
Tomcat started on port 8081 (http) with context path '/api'
```

---

## Running the frontend

From the `frontend/` directory:

```
npm install
ng serve
```

The application is available at **`http://localhost:4200`**.

Both applications must be running simultaneously.

---

## Default credentials

On **first startup against an empty database**, a default administrator is seeded automatically:

```
Username: admin
Password: Admin123!
```

The seeder only runs when the `users` table is empty, so restarting the application will never create duplicates or overwrite a changed password.

There is no self-registration endpoint by design — requirement 2 places user creation under admin control. Additional users are created through the admin UI at **Users → New user**.

---

## Project structure

```
Task-Manager-System/
├── backend/
│   └── src/main/java/com/example/taskmanager/
│       ├── config/          Security, CORS, WebSocket, password encoder, data seeder
│       ├── controller/      8 REST controllers
│       ├── exception/       Custom exceptions + @RestControllerAdvice
│       ├── model/
│       │   ├── converter/   JPA AttributeConverters for integer-backed enums
│       │   ├── dto/         request/ and response/ subpackages
│       │   ├── entity/      BaseEntity, SoftDeletableEntity, 5 entities
│       │   ├── enums/       Role, TaskStatus, NotificationType, ActionType
│       │   └── mapper/      Interfaces + impl/ (one per entity)
│       ├── repository/      5 Spring Data JPA repositories
│       ├── security/        JWT service, HTTP filter, STOMP interceptor, UserDetailsService
│       └── service/         Interfaces + impl/ (7 services)
│
└── frontend/
    └── src/app/
        ├── core/
        │   ├── models/      TypeScript interfaces mirroring the backend DTOs
        │   ├── services/    8 HTTP + WebSocket services
        │   ├── guards/      authGuard, adminGuard, guestGuard
        │   ├── interceptors/ authInterceptor, errorInterceptor
        │   └── utils/       Shared helpers
        ├── shared/components/  Layout, comment thread, status badge, dialogs, bell
        └── features/
            ├── auth/        Login
            ├── admin/       Dashboard, users, tasks, activity log
            ├── tasks/       Task detail (shared by both roles)
            ├── user/        My tasks
            └── profile/     Profile management
```

Both layers follow the same convention: an interface in the parent package, its implementation in an `impl/` subpackage.

### Data model

| Table | Soft delete | Relationships |
|---|---|---|
| `users` | Yes | — |
| `tasks` | Yes | → `users` (assignee) |
| `comments` | Yes | → `tasks`, → `users`, → `comments` (self-referencing parent) |
| `notifications` | Yes | → `users` (recipient), → `tasks` |
| `activity_logs` | **No — append-only by design** | → `users` (nullable) |

---

## API overview

All endpoints are prefixed with `/api`.

### Authentication
| Method | Endpoint | Access |
|---|---|---|
| POST | `/auth/login` | Public |
| POST | `/auth/logout` | Authenticated |

### Users (admin only)
| Method | Endpoint |
|---|---|
| GET | `/users` — paginated, filter by search/role/active |
| GET | `/users/{id}` |
| POST | `/users` |
| PUT | `/users/{id}` |
| DELETE | `/users/{id}` |

### Tasks
| Method | Endpoint | Access |
|---|---|---|
| GET | `/tasks` | Both — automatically scoped to the caller's own tasks for non-admins |
| GET | `/tasks/{id}` | Both — ownership enforced |
| POST | `/tasks` | Admin |
| PUT | `/tasks/{id}` | Admin |
| PATCH | `/tasks/{id}/status` | Both — ownership enforced |
| DELETE | `/tasks/{id}` | Admin |

### Comments
| Method | Endpoint | Access |
|---|---|---|
| GET | `/tasks/{taskId}/comments` | Both — ownership enforced |
| POST | `/tasks/{taskId}/comments` | Both — ownership enforced |

### Profile
| Method | Endpoint |
|---|---|
| GET | `/profile` |
| PUT | `/profile` |

### Notifications
| Method | Endpoint |
|---|---|
| GET | `/notifications` |
| GET | `/notifications/unread-count` |
| PATCH | `/notifications/{id}/read` |
| PATCH | `/notifications/read-all` |

### Admin dashboard
| Method | Endpoint |
|---|---|
| GET | `/dashboard/stats` |
| GET | `/activity-logs` |

### WebSocket
| Endpoint | Purpose |
|---|---|
| `ws://localhost:8081/api/ws` | STOMP connection (JWT sent as a CONNECT header) |
| `/user/queue/notifications` | Per-user notification destination |

---

## Design decisions

### Soft delete

Users, tasks, comments, and notifications are never physically removed. Deleting sets a `deleted_at` timestamp, and Hibernate's `@SQLRestriction("deleted_at IS NULL")` filters those rows from **every** query automatically.

This avoids two problems a hard delete would create:

- Deleting a user who has assigned tasks would either violate a foreign key constraint or, with `ON DELETE CASCADE`, destroy their task history.
- Activity log entries reference users. Hard deletion would break or erase the audit trail.

`ActivityLog` deliberately **does not** extend `SoftDeletableEntity` and has no `deleted_at` column — an audit log that can be hidden is not an audit log. This distinction is enforced in the type hierarchy rather than by convention.

### Denormalized `username` in the activity log

`activity_logs` stores both a `user_id` foreign key **and** a plain `username` string. This is intentional.

Because `User` carries a soft-delete restriction, joining to it filters out deleted users. Queries use `LEFT JOIN FETCH` so the log row survives with a null user, and the `username` snapshot — captured at write time — still identifies who performed the action. The UI displays the username with a "deleted user" badge.

The snapshot also preserves historical accuracy: if a user later changes their name, past log entries still show who they were at the time.

### Integer-backed enums

`Role`, `TaskStatus`, `NotificationType`, and `ActionType` are Java enums persisted as integers via JPA `AttributeConverter` implementations.

Each constant carries an **explicitly declared** id (`PENDING(1)`, `IN_PROGRESS(2)`, `COMPLETED(3)`) rather than relying on `EnumType.ORDINAL`, which derives the number from declaration order. With ORDINAL, inserting a new constant would silently change the meaning of every existing row. Explicit ids make reordering safe.

The API contract is unaffected — Jackson serializes enums by name, so clients still send and receive `"COMPLETED"`.

### Lazy loading and `open-in-view: false`

Spring's `open-in-view` is **disabled**. This closes the Hibernate session when the service method returns, so touching an unloaded lazy proxy afterwards throws immediately rather than silently firing an extra query during JSON serialization.

The consequence is that repositories fetch related data explicitly:

```java
@Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignedUser WHERE t.id = :id")
```

`LEFT` versus inner join is chosen per relationship nullability — an inner join on tasks would silently drop unassigned tasks from every list.

No entity is ever returned from a controller; all responses are DTOs, so no lazy proxy can reach Jackson.

### Server-side ownership enforcement

Requirement 6 states that users may only access tasks assigned to them. This is enforced in the **service layer**, not the UI.

In `TaskServiceImpl.searchTasks`, a non-admin's `assignedUserId` parameter is **overwritten** with their own id rather than defaulted:

```java
Long effectiveAssignedUserId = isAdmin(currentUser) ? assignedUserId : currentUser.getId();
```

A user requesting `GET /api/tasks?assignedUserId=3` therefore receives their own tasks, not user 3's. Single-task access and comment endpoints perform an equivalent ownership check.

Angular's route guards are a usability feature only — they prevent navigating to pages that would return errors. All authorization is enforced by the backend, where the role is read from the signed JWT rather than from client storage.

### Transaction handling for the activity log

Activity log writes **participate in the caller's transaction** rather than running in a new one. An earlier implementation used `Propagation.REQUIRES_NEW`, which caused a database deadlock when an administrator edited their own account: the outer transaction held a row lock on `users`, and the suspended inner transaction needed a shared lock on the same row for its foreign key.

Joining the transaction also guarantees the audit trail never records an operation that was subsequently rolled back.

### Threaded comments

`Comment` is self-referencing via a nullable `parent_id`. The entire thread is fetched in **one** query and assembled into a tree in memory using a `Map<Long, CommentResponseDTO>` — an O(n) two-pass algorithm.

The alternative — recursively querying for each comment's replies — would issue one query per node.

Nesting depth is **unlimited in the data model**. The Angular component caps *visual* indentation at three levels; deeper replies render flush-left so threads remain readable on small screens.

### JWT storage

The token is stored in `localStorage`, chosen for simplicity and to survive page refreshes.

**Acknowledged trade-off:** `localStorage` is readable by any JavaScript running on the page, so a cross-site scripting vulnerability would expose the token. An `httpOnly` cookie cannot be read by JavaScript at all, but requires CSRF protection and additional cookie configuration.

CSRF protection is disabled in `SecurityConfig`, which is correct **specifically because** authentication uses an `Authorization` header rather than cookies — CSRF attacks rely on the browser automatically attaching credentials.

### WebSocket transport

Native WebSocket is used rather than SockJS. SockJS provides HTTP long-polling fallback for networks that block WebSocket connections, which is unnecessary for a local deployment and required a `global` polyfill incompatible with Angular's build tooling.

The JWT is sent as a STOMP `CONNECT` frame header, since browsers do not permit setting HTTP headers on a WebSocket handshake. `JwtChannelInterceptor` validates it and binds the authenticated principal to the session, which is what allows Spring to route messages to per-user destinations.

### Schema management

`ddl-auto: update` lets Hibernate create and extend tables from the entity definitions, which keeps local setup to a single command.

A production deployment would use `validate` together with versioned migrations (Flyway or Liquibase), since `update` only ever adds — it does not handle renames, type changes, or column removal.

---

## Known limitations

- **Unique constraints and soft delete interact.** The `UNIQUE` index on `users.email` still applies to soft-deleted rows, so a deleted user's email cannot be reused. A production system would mangle the email on deletion or use a conditional index.

- **JWTs cannot be revoked.** Logout is client-side; the token remains valid until it expires (24 hours). Production systems mitigate this with short-lived tokens plus refresh tokens, or a server-side denylist. The `/auth/logout` endpoint exists to record the event in the activity log.

- **The in-memory STOMP broker is single-instance.** Notifications would not be delivered across multiple server instances. A production deployment would use an external broker such as RabbitMQ.

- **The assignee dropdown loads up to 100 users.** A larger deployment would use a searchable autocomplete backed by a paginated endpoint.

- **My Tasks status counts reflect the current page only.** The admin dashboard statistics are computed server-side and are accurate across all records.

- **Development runs over plain HTTP.** Passwords are transmitted in the request body and hashed with BCrypt server-side; a deployment must use HTTPS so credentials are encrypted in transit.
