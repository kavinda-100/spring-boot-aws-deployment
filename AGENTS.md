# Repository Guidelines

## Project Structure & Module Organization

This repository contains a single Spring Boot web service built with Maven. Production Java code lives in `src/main/java/com/kavinda/spring_aws_deploy/`; `SpringAwsDeployApplication` is the entry point, and controllers such as `BaseController` expose HTTP endpoints. Application configuration belongs in `src/main/resources/application.yaml`. Tests mirror the production package under `src/test/java/`. Maven writes generated artifacts to `target/`; do not commit that directory. Deployment-related files are kept at the repository root, notably `Dockerfile` and `README.md`.

## Build, Test, and Development Commands

Use the checked-in Maven Wrapper so contributors run a consistent Maven version:

- `./mvnw spring-boot:run` starts the service locally on port 8080.
- `./mvnw test` runs the JUnit test suite.
- `./mvnw clean package` runs tests and creates the executable JAR in `target/`.
- `docker build -t spring-aws-deploy .` builds the container image.
- `docker run --rm -p 8080:8080 spring-aws-deploy` runs that image locally.

After startup, verify `GET /` and `GET /health`, for example with `curl http://localhost:8080/health`.

## Coding Style & Naming Conventions

Target the Java version declared in `pom.xml` and keep the Docker build/runtime JDK versions compatible with it. Use four-space indentation, one public top-level class per file, and standard Java naming: `PascalCase` for classes, `camelCase` for methods and variables, and lowercase package names. Name Spring components by responsibility, such as `HealthController` or `DeploymentService`. Prefer constructor injection when dependencies are introduced. No formatter or linter is configured, so match nearby code and keep imports organized.

## Testing Guidelines

Tests use JUnit 5 through Spring Boot's test starter. Name test classes `*Tests` and test methods after observable behavior, such as `healthReturnsUpStatus`. Add focused MVC tests for endpoints and reserve `@SpringBootTest` for integration or context-loading checks. There is no enforced coverage threshold; changes should nevertheless cover new behavior and regressions.

## Commit & Pull Request Guidelines

Recent commits use short, imperative summaries such as `Add BaseController...` and `Initialize Spring Boot project...`. Follow that style and keep each commit focused. Pull requests should explain the change, list verification commands, link related issues, and include sample request/response output when API behavior changes. Call out configuration, Docker, port, or deployment impacts explicitly.

## Security & Configuration

Do not commit AWS credentials, secrets, or environment-specific values. Supply sensitive configuration through environment variables or deployment secrets, and document required variable names without including real values.
