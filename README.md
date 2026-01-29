# PayFlowX – Distributed Payment Orchestration Platform

PayFlowX is a backend-focused, production-style **payment orchestration system** designed to model real-world payment lifecycles in distributed financial systems. The platform supports **login, payment initiation, authorization, and asynchronous downstream processing** using an event-driven architecture.

The system demonstrates **centralized authentication enforcement, payment state management, idempotency, transactional consistency, event streaming with Kafka, and asynchronous execution with RabbitMQ**, implemented using Spring Boot and PostgreSQL.

---

## Microservices Overview

PayFlowX consists of **seven independently deployable microservices**, each owning its data and responsibilities.

| Service                   | Port   | Responsibility                                       |
| ------------------------- | ------ | ---------------------------------------------------- |
| **Eureka Server**         | 8761   | Service discovery and registration                   |
| **API Gateway**           | 8080   | Central entry point, JWT validation, request routing |
| **Auth Service**          | 8081   | Login-only authentication and JWT issuance           |
| **Payment Service**       | 8082   | Orchestrates payment lifecycle and publishes events  |
| **Authorization Service** | 8083   | Validates balance, limits, and payment rules         |
| **Ledger Service**        | 8084   | Maintains immutable financial ledger                 |
| **Analytics Service**     | 8085   | Builds read-optimized aggregates from events         |
| **Notification Service**  | 8086   | Sends payment notifications asynchronously           |

---

## High-Level Architecture

```mermaid
graph TB
    subgraph "Edge Layer"
        G[API Gateway :8080]
    end

    subgraph "Security"
        AS[Auth Service :8081]
    end

    subgraph "Core Domain"
        P[Payment Service :8082]
        AU[Authorization Service :8083]
    end

    subgraph "Messaging"
        K[Kafka :9092]
        R[RabbitMQ :5672]
    end

    subgraph "Downstream"
        L[Ledger Service :8084]
        N[Notification Service :8086]
        A[Analytics Service :8085]
    end

    subgraph "Discovery"
        E[Eureka Server :8761]
    end

    Client --> G
    G --> AS
    G --> P
    P --> AU
    P --> K
    P --> R

    K --> L
    K --> A
    R --> N

    AS -.-> E
    P -.-> E
    AU -.-> E
    L -.-> E
    A -.-> E
    N -.-> E
```

---

## Payment Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant G as API Gateway
    participant P as Payment Service
    participant AU as Authorization Service
    participant K as Kafka
    participant R as RabbitMQ
    participant L as Ledger Service
    participant A as Analytics Service
    participant N as Notification Service

    C->>G: POST /api/payments
    G->>P: Forward Request
    P->>P: Check Idempotency
    P->>P: Create Payment (INITIATED)
    P->>K: Publish payment.initiated
    P->>AU: Authorize Payment
    AU->>AU: Validate Balance/Limits
    AU-->>P: Authorization Response
    
    alt Authorized
        P->>P: Update Status (AUTHORIZED)
        P->>K: Publish payment.authorized
        P->>P: Update Status (COMPLETED)
        P->>K: Publish payment.completed
        P->>R: Send Notification Task
        P-->>C: Success Response
    else Rejected
        P->>P: Update Status (FAILED)
        P->>K: Publish payment.failed
        P->>R: Send Notification Task
        P-->>C: Failure Response
    end

    K-->>L: Consume Events
    K-->>A: Consume Events
    R-->>N: Consume Notification
```

---

## Tech Stack

| Layer              | Technology                          |
| ------------------ | ----------------------------------- |
| **Runtime**        | Java 17, Spring Boot 3.5            |
| **Service Discovery** | Netflix Eureka                   |
| **API Gateway**    | Spring Cloud Gateway                |
| **Database**       | PostgreSQL (one DB per service)     |
| **Event Streaming**| Apache Kafka                        |
| **Message Queue**  | RabbitMQ                            |
| **Inter-Service**  | OpenFeign                           |
| **Containerization** | Docker, Docker Compose            |

---

## Database Architecture

Each microservice owns its database following the **Database per Service** pattern:

| Service               | Database                  |
| --------------------- | ------------------------- |
| Auth Service          | `payflowx_users`          |
| Authorization Service | `payflowx_authorization`  |
| Payment Service       | `payflowx_payment`        |
| Ledger Service        | `payflowx_ledger`         |
| Analytics Service     | `payflowx_analytics`      |
| Notification Service  | `payflowx_notification`   |

---

## Key Features

### Idempotency
Prevents duplicate payments using client-provided `idempotencyKey`. If the same key is sent twice, the original payment response is returned.

### Payment State Machine
```
INITIATED → AUTHORIZED → COMPLETED
     ↓           ↓
   FAILED      FAILED
```

### Event-Driven Architecture
- **Kafka Topics**: `payment-events` for durable event streaming
- **RabbitMQ Queue**: `payment-notifications` for async notifications

### Event Types
| Event               | Published When              | Consumers                    |
| ------------------- | --------------------------- | ---------------------------- |
| `payment.initiated` | Payment record created      | Ledger, Analytics            |
| `payment.authorized`| Authorization approved      | Analytics                    |
| `payment.completed` | Payment fully processed     | Ledger, Analytics            |
| `payment.failed`    | Authorization rejected      | Ledger, Analytics            |

---

## Project Structure

```
payflowx/
├── docker-compose.yml
├── eureka-server/
├── api-gateway/
├── auth-service/
├── authorization-service/
├── payment-service/
├── ledger-service/
├── analytics-service/
└── notification-service/
```

---

