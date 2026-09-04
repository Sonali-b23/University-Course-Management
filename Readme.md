# University Course Management System

A full-stack University Course Management System built with Java Spring Boot for the backend and React for the frontend. This system enables efficient course management including creation, updating, deletion, and viewing, all backed by a MySQL database.

## 🚀 Features (Implemented)

- Account registration and login (JWT-based)
- Role-based access: anyone can browse courses, only an **ADMIN** account can add, update, or delete them
- Add new courses with title and description
- Update existing course details
- Delete courses (with a confirmation prompt)
- View all available courses
- **Course registration:** any logged-in account can enroll in and drop courses, and see their own enrolled courses -- an ADMIN can additionally see the full roster for a given course
- Server-side validation with clear field-level error messages
- Consistent JSON error responses (401/403/404/409/400 as appropriate) instead of raw stack traces

## 🛠 Tech Stack

- **Frontend:** React (Vite), Bootstrap, Axios, react-hot-toast, React Context (auth state)
- **Backend:** Spring Boot (Java 17), Spring Data JPA, Spring Security, JWT (jjwt), Bean Validation
- **Database:** MySQL (H2 in-memory for the backend's own test suite)
- **Containerization:** Docker + docker-compose for the whole stack

## 📌 What changed in this pass

The previous version worked for the happy path but had a few real bugs and some outdated practices. All of the following are fixed:

- **Course ids are now database-generated** (`@GeneratedValue`), not typed in by hand on the Add Course form. Previously, adding a course with an id that already existed silently overwrote that course with no warning -- this is now structurally impossible, since the create/update request body has no `id` field to begin with. (`Backend/src/test/java/.../dao/CourseDaoTest.java` pins this with a regression test.)
- **Proper HTTP status codes and error bodies.** Looking up, updating, or deleting a course that doesn't exist now returns a clean `404` with a JSON message, instead of either an unhandled 500 with a stack trace or (for delete) a misleading generic 500. Validation failures (e.g. a blank title) return `400` with per-field messages. See `GlobalExceptionHandler`.
- **Bean validation** on the request body (`title`/`description` required, length-capped) via `spring-boot-starter-validation`.
- **`PUT /courses/{courseId}`** now targets the course by URL, matching normal REST conventions (it used to be a bare `PUT /courses` with the id inside the body).
- **DB credentials are no longer hardcoded.** `application.properties` now reads `DB_USERNAME` / `DB_PASSWORD` / `DB_URL` / `CORS_ALLOWED_ORIGIN` / `SERVER_PORT` from the environment, falling back to convenient local-dev defaults. Never rely on those defaults outside your own machine.
- **CORS is scoped to the actual frontend origin** (configurable via `CORS_ALLOWED_ORIGIN`) instead of allowing any origin.
- **Modern MySQL driver coordinates** (`com.mysql:mysql-connector-j`, version managed by Spring Boot's own BOM) replacing the old, unmaintained `mysql:mysql-connector-java`.
- **The Maven wrapper actually works now.** `Backend/.mvn/wrapper/maven-wrapper.properties` was missing from the repo, so `./mvnw` as previously committed couldn't run at all.
- **Frontend cleanup:** removed `react-toastify`, which was mounted in `App.jsx` but never actually used for a single toast anywhere -- every real toast call used `react-hot-toast`, so it was dead weight (and its CSS was being imported twice). There's now one global `<Toaster/>` instead of one per page. Fixed a `<StrictMode>` that was imported but never actually wrapped around `<App/>`. Added `prop-types` and fixed all ESLint findings. The API base URL is now read from `VITE_API_BASE_URL` (see `.env.example`) instead of being hardcoded to `localhost:8082`.
- **`npm start` now works** -- the README told you to run it, but this is a Vite project and only `dev`/`build`/`preview` existed; added a `start` alias.
- **Docker support** for the whole stack (backend, frontend, MySQL) -- see below. This was already on the project roadmap.
- **A real backend test suite** (previously just the default empty "context loads" test) covering the id-generation regression, service-layer not-found handling, controller-level validation/status codes, and (new) authentication/authorization behavior end to end against the real security config.

## 🔐 Authentication & Authorization (new)

Previously, *anyone* who could reach the API could add, edit, or delete any course -- there was no login at all. Now:

- `POST /auth/register` creates a **USER** account (browse-only) and returns a JWT. Self-registration can never create an ADMIN -- there's no field for it.
- `POST /auth/login` returns a JWT for an existing account.
- The frontend attaches the JWT as `Authorization: Bearer <token>` on every request automatically (`src/api/httpClient.js`) once you're logged in, and stores it in `localStorage` (fine for a portfolio project; a production app would typically prefer an httpOnly cookie to reduce XSS exposure).
- `POST /courses`, `PUT /courses/{id}`, and `DELETE /courses/{id}` require an **ADMIN** account -- a logged-out request gets `401`, a logged-in non-admin gets `403`.
- **One default admin account is seeded automatically** on first backend startup if none exists (`config/AdminSeeder.java`), since there'd otherwise be no way to ever get an ADMIN account at all. Default: username `admin`, password `admin123` -- **change both** via `ADMIN_USERNAME` / `ADMIN_PASSWORD` env vars before running this anywhere but your own machine, and change the password after logging in (there's no change-password endpoint yet -- see Project Status).
- The JWT signing secret comes from `JWT_SECRET` (falls back to an insecure dev-only default -- see `application.properties`). Generate a real random secret (32+ bytes) for anything beyond local development.

## 📂 Project Structure

```
University-Course-Management/
├── Backend/                  # Spring Boot REST API
│   ├── src/main/java/in/at/main/
│   │   ├── controller/       # REST endpoints
│   │   ├── service/          # Business logic
│   │   ├── dao/               # Spring Data JPA repository
│   │   ├── entity/            # JPA entities
│   │   ├── dto/                # Request bodies (validation lives here)
│   │   ├── exception/         # Custom exceptions + global error handling
│   │   ├── security/           # JWT service, filter, Spring Security config
│   │   └── config/             # Default-admin seeding
│   ├── src/test/              # Unit + slice + security integration tests
│   └── Dockerfile
├── Frontend/                  # React (Vite) app
│   ├── src/Components/
│   ├── src/api/                # API base URL + auth-aware axios client
│   ├── src/context/            # AuthContext (login state, JWT storage)
│   ├── .env.example
│   └── Dockerfile
├── docker-compose.yml          # Runs MySQL + backend + frontend together
└── Readme.md
```

## 📡 API Endpoints

| Method | Path                  | Auth required        | Description                     |
|--------|------------------------|-----------------------|----------------------------------|
| GET    | `/home`                | No                    | Health/welcome message          |
| POST   | `/auth/register`       | No                    | Create a USER account (`{username, password}`) -- returns a JWT, `201` |
| POST   | `/auth/login`          | No                    | Log in (`{username, password}`) -- returns a JWT, `200` |
| GET    | `/courses`             | No                    | List all courses                |
| GET    | `/course/{courseId}`   | No                    | Get a single course (404 if missing) |
| POST   | `/courses`             | **ADMIN**             | Create a course (`{title, description}`) -- returns `201` |
| PUT    | `/courses/{courseId}`  | **ADMIN**             | Update a course (`{title, description}`) |
| DELETE | `/courses/{courseId}`  | **ADMIN**             | Delete a course -- returns `204` |
| POST   | `/enrollments`         | Logged in             | Enroll in a course (`{courseId}`) -- `201`, `409` if already enrolled |
| DELETE | `/enrollments/{courseId}` | Logged in          | Drop a course -- `204`, `404` if not enrolled |
| GET    | `/enrollments/me`      | Logged in             | List the courses *you're* enrolled in |
| GET    | `/courses/{courseId}/enrollments` | **ADMIN**  | List everyone enrolled in a course |

## 💻 Getting Started

### Prerequisites

- Java 17+
- Node.js and npm
- MySQL server (or Docker -- see below)

### Option A: Run with Docker (recommended)

```bash
docker compose up --build
```

This starts MySQL, the backend (`http://localhost:8082`), and the frontend (`http://localhost:5173`) together, wired up with the right environment variables. First startup takes a bit longer while MySQL initializes.

### Option B: Run locally

1. **Clone the repository:**

   ```bash
   git clone https://github.com/Sonali-b23/University-Course-Management.git
   cd University-Course-Management
   ```

2. **Backend:**

   - Make sure a local MySQL instance is running with a `restdb` database, or set `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` env vars to point at your own instance.
   - Run:
     ```bash
     cd Backend
     ./mvnw spring-boot:run
     ```
     If `./mvnw` gives you trouble (Maven Wrapper needs to download things on first run), a locally-installed Maven 3.9+ works identically: `mvn spring-boot:run`.

3. **Frontend:**

   ```bash
   cd Frontend
   cp .env.example .env   # adjust VITE_API_BASE_URL if your backend isn't on localhost:8082
   npm install
   npm run dev             # (npm start also works)
   ```

Both the backend and frontend need to be running at the same time for the app to work end to end.

## 🧪 Testing

```bash
cd Backend
./mvnw test
```

Covers: id auto-generation (the regression test for the original overwrite bug, against a real in-memory H2 database), service-layer 404 handling for get/update/delete on a missing course, controller-level status codes/validation (`400` on a blank title, `201` on create, `404` on a missing course, `204` on delete), registration/login (`AuthControllerTest`: duplicate username → `409`, weak password → `400`, wrong password → `401`), end-to-end authorization against the *real* security config rather than a mocked one (`SecurityIntegrationTest`: no token → `401`, logged-in non-admin → `403`, logged-in admin → `201`), and course registration end to end (`EnrollmentIntegrationTest`: no token → `401`, enroll → `201`, double-enroll → `409`, drop when not enrolled → `404`, drop then re-check `/enrollments/me` is empty, a plain user blocked from `/courses/{id}/enrollments` → `403`, an admin allowed → `200`), plus unit coverage for the enrollment service layer (`EnrollmentServiceImplTest`).

**Verified:** `mvn test` passes clean -- `Tests run: 33, Failures: 0, Errors: 0, Skipped: 0`. This test suite (and the rest of the backend changes in this pass) was originally written in an environment that couldn't reach Maven Central to compile or run it, so a few real issues only surfaced once it was actually built locally -- all since fixed and confirmed by a real green run: an invalid XML comment in `pom.xml` that blocked Maven from even parsing the project; `JwtService` being written against jjwt's older 0.11.x API (`Jwts.parserBuilder()...`) when `pom.xml` pins the 0.12.x line, which renamed that API (now uses `Jwts.builder().claims()...signWith(key)` / `Jwts.parser().verifyWith(key).build().parseSignedClaims(...)`); the `User` entity defaulting to a table named `user`, which is a reserved word in H2 (the test database) and broke every query touching it (fixed with `@Table(name = "users")`); `MyControllerTest`'s `@WebMvcTest` slice not mocking out the security beans its auto-detected `SecurityConfig` still needs to construct even with filters disabled; and a Byte Buddy/Mockito version that didn't yet recognize very new JDKs (worked around via `-Dnet.bytebuddy.experimental=true` on the surefire plugin -- see `pom.xml`).

The frontend's build and lint *were* verified end to end (`npm run build`, `npm run lint` both pass clean) in the same pass, since npm's registry was reachable there.

## 📦 Dependencies

Frontend dependencies (installed via `npm install`, see `Frontend/package.json`):
`axios`, `bootstrap`, `react-bootstrap`, `react-router-dom`, `react-hot-toast`, `prop-types`

Backend dependencies added for auth (see `Backend/pom.xml`): `spring-boot-starter-security`, `io.jsonwebtoken:jjwt-{api,impl,jackson}`, `spring-security-test` (test scope).

## 📌 Author

- **Bora Sonali**
- GitHub: [Sonali-b23](https://github.com/Sonali-b23)

## Project Status

The University Course Management System now has: course CRUD with proper error handling and validation, JWT-based login/registration, role-based access control (ADMIN-only writes), course registration (students enrolling in and dropping courses, not just admins managing a catalog), and Docker support. Still on the roadmap:

- **A change-password / basic account management endpoint** -- right now the seeded admin password can only be changed via `ADMIN_PASSWORD` + recreating the database, which isn't a real workflow.
- **Fuller admin controls** -- e.g. promoting a USER to ADMIN (currently only possible by editing the database directly), listing/managing accounts.
- **Pagination and search** on the course list -- fine for a handful of courses, but `findAll()` with no limit won't scale.
- API documentation (e.g. springdoc-openapi / Swagger UI) -- deliberately not added since it needed a dependency version this environment couldn't verify against Maven Central; worth adding once you can build locally.

These will further improve the system's usability and completeness.

Feel free to contribute or open issues!
