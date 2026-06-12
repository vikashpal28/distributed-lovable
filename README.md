# Distributed Lovable

A microservices-based clone of the Lovable platform built with Spring Boot, Spring Cloud, and a React/Vite frontend companion.

## Repository Structure

- `account-service/` — account management microservice
- `api-gateway/` — API gateway using Spring Cloud Gateway
- `common-lib/` — shared Java library for cross-service models and utilities
- `config-service/` — externalized configuration service
- `discovery-service/` — service registry for Eureka discovery
- `intelligence-service/` — intelligence/AI microservice
- `workspace-service/` — workspace management microservice
- `frontend/project-companion/` — web frontend app built with Vite + React
- `k8s/` — Kubernetes manifests for infra, services, proxy, and stateful resources

## Technology Stack

- Java 21
- Spring Boot 4.0.6
- Spring Cloud 2025.1.1
- Netflix Eureka, Spring Cloud Config, OpenFeign, Spring Cloud Gateway
- PostgreSQL, Redis, Kafka, MinIO
- React, Vite, Tailwind CSS

## Prerequisites

- Java 21 SDK
- Maven or use the included Maven wrapper (`./mvnw`)
- Node.js / Bun for the frontend
- Docker / Kubernetes if deploying from `k8s/`

## Build and Run

### Backend services

Each Java service contains its own Maven wrapper. From the repository root, build a service with:

```bash
./<service>/mvnw clean package
```

Example:

```bash
./discovery-service/mvnw clean package
```

Run a service locally with:

```bash
cd <service>
./mvnw spring-boot:run
```

### Frontend

The frontend app lives in `frontend/project-companion/`.

Install dependencies and start development server with:

```bash
cd frontend/project-companion
npm install
npm run dev
```

If you use Bun, you can also use:

```bash
cd frontend/project-companion
bun install
bun run dev
```

## Testing

Run Java tests per service:

```bash
./<service>/mvnw test
```

Run frontend tests from the frontend package:

```bash
cd frontend/project-companion
npm run test
```

## Kubernetes

Kubernetes deployment manifests are located under `k8s/`:

- `k8s/infra/`
- `k8s/proxy/`
- `k8s/services/`
- `k8s/statefull/`

## Notes

- Each module includes its own `HELP.md` for module-specific instructions.
- Shared code is implemented in `common-lib/` and consumed by the backend services.
- Service configuration is typically found under `src/main/resources` in each Java module.

## Contribution

1. Make your changes in the appropriate module.
2. Run the module tests.
3. Update documentation or Kubernetes manifests if needed.
