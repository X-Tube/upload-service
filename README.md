# Upload Service

A microservice dedicated to handling high-performance file uploads, supporting multipart transfers to S3 and asynchronous event orchestration via Kafka.

## 🏗️ Architecture

This service acts as an orchestrator, managing multipart upload states and triggering downstream processing through distributed events.

* **Storage:** AWS S3 (via LocalStack in development)
* **Messaging:** Apache Kafka
* **Mapping:** MapStruct

## 🚀 Getting Started

### Prerequisites

* Java 21
* Docker & Docker Compose
* Maven 3.x

### Running Locally

1. Spin up the infrastructure: `docker-compose up -d`
2. Run the application: `./mvnw spring-boot:run`

## 🧪 Testing

We use Testcontainers to ensure our integration with Kafka and S3 is authentic.

* **Run all tests:** `./mvnw test`
* **Note:** Tests require a running Docker daemon. They automatically spin up a Kafka broker and a LocalStack instance.

## 🔭 Observability

This service implements the three pillars of observability to ensure system health and rapid debugging.

| Pillar | Implementation | Purpose |
| :--- | :--- | :--- |
| **Logs** | Structured (JSON) logs | Centralized event tracking and error analysis. |
| **Metrics** | Spring Boot Actuator | Tracking latency, request rates, and resource utilization. |
| **Traces** | OpenTelemetry | Visualizing request flow across the distributed system. |

### Accessing Observability Tools

* **Actuator Health:** `GET /actuator/health`
* **Metrics Dashboard:** `Soon`
* **Distributed Traces:** `Soon`

## ⚙️ Configuration

| Variable | Default | Description |
| :--- | :--- | :--- |
| `AWS_S3_ENDPOINT` | `http://localhost:9000` | S3 compatible endpoint |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |
| `AWS_S3_PRESIGNED_DURATION` | `15` | Minutes for presigned URL validity |