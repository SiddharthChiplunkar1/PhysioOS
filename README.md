# PhysioOS 🚀

PhysioOS is a production-grade, highly scalable, multi-tenant microservices platform designed for modern physiotherapy clinics. It handles everything from clinical documentation and appointment scheduling to billing, inventory management, and telemetry ingestion.

## 🏗️ Architecture

PhysioOS is built as a **Java Monorepo** containing **31 Spring Boot Microservices**. The architecture is heavily decoupled, event-driven (via Apache Kafka), and relies on a decentralized data strategy where each microservice owns its domain data.

### Key Technologies
* **Language:** Java 25 (compiled targeting LTS release 21)
* **Framework:** Spring Boot 3.3.4, Spring Cloud 2023.0.0
* **Data Stores:** PostgreSQL 15, Redis 7 (Caching)
* **Messaging:** Confluent Kafka 7.4.0 (Event-Driven Workflows)
* **Security:** Argon2id Password Hashing, JWT Auth, OWASP ASVS Validation
* **Observability:** Prometheus, Grafana, Loki, Tempo, Micrometer (OpenTelemetry)
* **CI/CD & Security:** GitHub Actions, Trivy (SBOM & Vulnerability Scanning)
* **Deployment:** Kubernetes (Helm), Docker Multi-stage Builds

---

## 🛠️ Project Structure

Currently, **12 core domain services** have been fully implemented with complete business logic, DTO validation, entities, state machines, and robust automated test suites:

| Service | Responsibility |
| :--- | :--- |
| **`api-gateway`** | Spring Cloud Gateway routing & edge security |
| **`identity-service`** | JWT Auth, Argon2id hashing, 7-day refresh token rotation |
| **`organization-service`**| Multi-tenancy isolation and tenant management |
| **`patient-service`** | Core relational data modeling for clinical patients |
| **`employee-service`** | Complex state machines for staff (Active, Suspended, etc.) |
| **`appointment-service`** | Heavy scheduling engine mathematically blocking double-bookings |
| **`billing-service`** | Financial core utilizing `BigDecimal` for flawless tax/discount calculations |
| **`inventory-service`** | Immutable ledger for tracking stock movements and shrinkage |
| **`document-service`** | S3 metadata abstraction with strict OWASP MIME-type regex validation |
| **`health-metrics-service`**| Telemetry ingestion designed to reject impossible time-series data |
| **`feature-flag-service`** | Dynamic configuration engine to toggle features in production |
| **`common`** | Shared DTOs, Role Enums, and the `GlobalExceptionHandler` |

> *Note: The remaining 19 microservices (e.g. `ai-service`, `workflow-service`) are fully scaffolded, successfully building in the pipeline, and ready for development!*

---

## 🚀 Getting Started

### Prerequisites
1. **Java Development Kit (JDK):** Version 25 (or at least 21)
2. **Maven:** 3.9+
3. **Docker Desktop:** With Docker Compose v2+
4. **Git**

### 1. Build the Monorepo
The project is configured with a global `<pluginManagement>` structure that ensures absolute consistency across all 31 modules.

```bash
# Clone the repository
git clone https://github.com/SiddharthChiplunkar1/PhysioOS.git
cd PhysioOS

# Build the entire platform, run all tests, and package JARs
mvn clean install
```

### 2. Spin up Local Infrastructure
PhysioOS provides a unified `docker-compose.yml` to instantly spin up the data planes, messaging queues, and observability stack. 

```bash
# Start PostgreSQL, Redis, Kafka, and Grafana Stack in the background
docker-compose up -d
```
*(The custom PostgreSQL init script will automatically bootstrap a dedicated database for every microservice!)*

### 3. Run a Service
You can start any service using the Spring Boot Maven plugin or run the `.jar` directly.

```bash
# Example: Start the Identity Service
mvn spring-boot:run -pl identity-service
```

---

## 🛡️ CI/CD & Production Readiness

PhysioOS takes a non-compromising stance on production security and reliability.

### Automated CI/CD (GitHub Actions)
Every PR and push to `main` triggers our comprehensive pipeline (`.github/workflows/ci.yml`):
1. **Build & Test:** Compiles all 31 modules and runs JUnit 5 / Mockito unit and `@WebMvcTest` integration tests.
2. **Dependency & SAST Scanning:** Performs lightning-fast vulnerability filesystem scans using **Trivy**.
3. **Containerization:** Builds optimized, non-root Docker images for the services.
4. **SBOM Generation:** Generates Software Bill of Materials natively via Trivy.
5. **Helm Validation:** Performs a dry-run `helm template` to mathematically prove the Kubernetes manifests are syntactically perfect.

### Kubernetes Deployment
A highly generic, production-ready Helm chart is provided at `k8s/charts/physioos-service`. Rather than writing 30 separate manifests, this single chart parametrizes the deployment of any microservice in the monorepo, complete with Horizontal Pod Autoscaling (HPA) and Resource Quotas.
