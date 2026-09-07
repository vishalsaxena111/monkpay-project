# monkpay Clone - Distributed Payment System

A Spring Boot microservices project that models the core flow of a payment gateway similar to monkpay. The system is split into independently deployable services for merchant onboarding, API key authentication, order creation, payment processing, card tokenization, settlement, webhook delivery, service discovery, centralized configuration, and observability.

This repository is intended as a learning and portfolio project for distributed backend design. It demonstrates service-to-service communication, shared library packaging, API gateway authentication, idempotency, rate limiting, event-driven processing, and operational monitoring.

## Architecture

```text
Client / Merchant
      |
      v
API Gateway Service
      |
      +--> Merchant Service
      +--> Payment Service
      +--> Vault Service
      +--> Operations Service

Supporting services:
Config Service  -> centralized externalized configuration
Discovery Service -> Eureka service registry
Common Lib -> shared DTOs, enums, filters, exceptions, utilities
Observability -> Zipkin, Prometheus, Grafana
```

## Modules

| Module | Purpose |
| --- | --- |
| `api-gateway-service` | Entry point for external traffic. Handles JWT/API-key authentication, public route matching, header enrichment, and routing to backend services. |
| `merchant-service` | Merchant signup/login, JWT generation, merchant/customer lookup, API key creation, rotation, revocation, and webhook configuration. |
| `payment-service` | Order creation, payment initiation, capture flow, payment state machine, gateway adapter routing, outbox publishing, and settlement lookup APIs. |
| `vault-service` | Card tokenization and secure card storage abstraction. Validates PAN, CVV, expiry details, and returns reusable payment tokens. |
| `operations-service` | Settlement processing, webhook delivery, retry handling, dead-letter recording, and dummy webhook endpoint for local testing. |
| `common-lib` | Shared code used by services: enums, DTOs, money type, exception handling, audit support, rate limiting, idempotency, Redis helpers, Kafka config, signing utilities, and merchant context filters. |
| `config-service` | Spring Cloud Config Server. Reads centralized configuration from an external Git repository. |
| `discovery-service` | Eureka registry for service discovery. Runs on port `8761`. |
| `observability` | Docker Compose setup for Zipkin, Prometheus, and Grafana. |

## Tech Stack

- Java 25
- Spring Boot 4.1.0
- Spring Cloud 2025.1.2
- Spring Cloud Config
- Netflix Eureka
- Spring Cloud Gateway MVC
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Redis
- Kafka
- OpenFeign
- Resilience4j
- ShedLock
- MapStruct
- Lombok
- Micrometer, Prometheus, Grafana, Zipkin

## Main Flows

### Merchant Onboarding

1. Merchant signs up through `merchant-service`.
2. Merchant logs in and receives a JWT.
3. Merchant creates an API key for `TEST` or `LIVE`.
4. API Gateway authenticates future requests using JWT or API key depending on the route.

### Order and Payment Flow

1. Merchant creates an order with amount, receipt, notes, and optional customer details.
2. Merchant initiates a payment for the order using `CARD`, `NETBANKING`, `UPI`, or `WALLET`.
3. Payment service routes the request to the configured payment processor/adapter.
4. State transitions are recorded through the payment state machine.
5. Outbox events are published for downstream settlement and webhook processing.

### Card Tokenization

1. Merchant submits card details to `vault-service`.
2. Vault validates PAN, CVV, expiry month/year, and optional card holder details.
3. Vault stores the sensitive card reference and returns a token.
4. Future card payments can use the token instead of raw card data.

### Settlement and Webhooks

1. Operations service groups eligible payments into settlement records.
2. A bank transfer processor simulates settlement execution.
3. Webhook events are delivered to configured merchant endpoints.
4. Failed webhooks are retried and eventually recorded in the dead-letter queue.

## Public API Overview

The gateway is intended to be the public entry point. Actual ports and route mappings are expected to come from the external config repository served by `config-service`.

### Authentication

```http
POST /v1/auth/signup
POST /v1/auth/login
```

Signup request:

```json
{
  "name": "Demo Merchant",
  "email": "merchant@example.com",
  "password": "password123",
  "businessName": "Demo Store",
  "businessType": "PRIVATE_LIMITED"
}
```

Login request:

```json
{
  "email": "merchant@example.com",
  "password": "password123"
}
```

### API Keys

```http
POST   /v1/merchants/api-keys
GET    /v1/merchants/api-keys
DELETE /v1/merchants/api-keys/keyId
POST   /v1/merchants/api-keys/{keyId}/rotate
```

Create API key request:

```json
{
  "environment": "TEST"
}
```

> Note: the delete mapping currently uses `/keyId` literally in code while the method expects a `@PathVariable UUID keyId`. It likely should be `/{keyId}`.

### Webhook Configuration

```http
POST   /v1/merchants/webhooks
GET    /v1/merchants/webhooks
GET    /v1/merchants/webhooks/{id}
PUT    /v1/merchants/webhooks/{id}
DELETE /v1/merchants/webhooks/{id}
```

Create or update webhook request:

```json
{
  "targetUrl": "https://merchant.example.com/webhooks/monkpay",
  "eventTypes": "PAYMENT_STATUS_CHANGED,REFUND_CREATED"
}
```

Use `ALL`, `null`, or a blank value for `eventTypes` to subscribe to every event type.

### Orders

```http
POST /v1/orders
```

Create order request:

```json
{
  "amount": {
    "amountUnits": 50000,
    "currency": "INR"
  },
  "receipt": "order_rcpt_001",
  "notes": {
    "source": "checkout"
  },
  "customer": {
    "name": "Customer Name",
    "email": "customer@example.com",
    "phone": "9999999999"
  }
}
```

### Payments

```http
POST /v1/payments
POST /v1/payments/{paymentId}/capture
```

Initiate payment request:

```json
{
  "orderId": "00000000-0000-0000-0000-000000000000",
  "method": "UPI",
  "methodDetails": {
    "vpa": "customer@upi"
  }
}
```

Optional idempotency header:

```http
X-Idempotency-Key: unique-request-key
```

### Vault

```http
POST /v1/vault/tokenize
```

Tokenize card request:

```json
{
  "pan": "4111111111111111",
  "cvv": "123",
  "expiryMonth": 12,
  "expiryYear": 2030,
  "customerId": "00000000-0000-0000-0000-000000000000",
  "cardHolderName": "Customer Name"
}
```

## Local Setup

### Prerequisites

- JDK 25
- Maven 3.9+
- Docker and Docker Compose
- PostgreSQL
- Redis
- Kafka
- GitHub access token for the external config repository if it is private

### Configuration

The service-specific runtime configuration is loaded from Spring Cloud Config:

```yaml
spring:
  config:
    import: configserver:http://localhost:8888
```

`config-service` reads from:

```text
https://github.com/Anuj-Kumar-Sharma/distributed-razrorpay-config
```

Configure Git credentials through environment variables instead of hard-coding secrets:

```powershell
$env:CONFIG_GIT_USERNAME = "your-github-username"
$env:CONFIG_GIT_PASSWORD = "your-github-token"
```

### Build Common Library

Each service depends on `common-lib` version `1.0.0`, so install it into the local Maven repository first:

```powershell
cd common-lib
.\mvnw clean install
```

### Run Infrastructure

Start observability tools:

```powershell
cd observability
docker compose up -d
```

Default URLs:

| Tool | URL |
| --- | --- |
| Zipkin | `http://localhost:9411` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3000` |

Grafana local credentials are `admin` / `admin` as defined in `observability/docker-compose.yml`.

PostgreSQL, Redis, and Kafka must also be running according to the values provided by the external config repository.

### Run Services

Start services in this order:

1. `config-service`
2. `discovery-service`
3. `common-lib` is already installed, not run as a service
4. `merchant-service`
5. `vault-service`
6. `payment-service`
7. `operations-service`
8. `api-gateway-service`

Example:

```powershell
cd config-service
.\mvnw spring-boot:run
```

Run the same command from each service directory in separate terminals.

Known local ports from checked-in config:

| Service | Port |
| --- | --- |
| Config Service | `8888` |
| Discovery Service | `8761` |

Other service ports are expected to come from the external config repository.

## Testing

Run tests for an individual service:

```powershell
cd payment-service
.\mvnw test
```

Run tests for all services manually from each module:

```powershell
cd common-lib; .\mvnw test
cd ..\merchant-service; .\mvnw test
cd ..\vault-service; .\mvnw test
cd ..\payment-service; .\mvnw test
cd ..\operations-service; .\mvnw test
cd ..\api-gateway-service; .\mvnw test
cd ..\config-service; .\mvnw test
cd ..\discovery-service; .\mvnw test
```

## Security Notes

- Do not commit real GitHub tokens, JWT secrets, database passwords, encryption keys, API keys, or webhook signing secrets.
- The config server now reads Git credentials from `CONFIG_GIT_USERNAME` and `CONFIG_GIT_PASSWORD`.
- If a real token was previously committed, revoke it in GitHub immediately and create a new one.
- Card data in this project is for simulation and learning. Do not use this implementation to process real payment card data.
- Use HTTPS, secret management, key rotation, audit logging, and PCI-DSS-compliant storage before any production use.

## Repository Notes

- This repository currently uses separate Maven projects per service rather than a single parent multi-module build.
- `common-lib` must be installed locally before dependent services can compile.
- Build output directories such as `target/`, IDE metadata, and OS files are ignored by the root `.gitignore`.
- The project uses generated code through Lombok and MapStruct, so annotation processing must be enabled in the IDE.

## Suggested Improvements

- Add a root Maven parent project to build all modules with one command.
- Add Docker Compose for PostgreSQL, Redis, Kafka, and all application services.
- Move all secrets to environment variables or a dedicated secret manager.
- Fix the API key delete route from `/keyId` to `/{keyId}`.
- Add OpenAPI/Swagger documentation for every public endpoint.
- Add integration tests with Testcontainers for PostgreSQL, Redis, Kafka, and service-to-service flows.
