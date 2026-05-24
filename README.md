<!DOCTYPE html>
<html lang="en">
<body>

    <h1>Upload Service</h1>
    <p>A microservice dedicated to handling high-performance file uploads, supporting multipart transfers to S3 and asynchronous event orchestration via Kafka.</p>

    <h2>🏗️ Architecture</h2>
    <p>This service acts as an orchestrator, managing multipart upload states and triggering downstream processing through distributed events.</p>
    <ul>
        <li><strong>Storage:</strong> AWS S3 (via LocalStack in development)</li>
        <li><strong>Messaging:</strong> Apache Kafka</li>
        <li><strong>Mapping:</strong> MapStruct</li>
    </ul>

    <h2>🚀 Getting Started</h2>
    <h3>Prerequisites</h3>
    <ul>
        <li>Java 21</li>
        <li>Docker & Docker Compose</li>
        <li>Maven 3.x</li>
    </ul>

    <h3>Running Locally</h3>
    <ol>
        <li>Spin up the infrastructure: <code>docker-compose up -d</code></li>
        <li>Run the application: <code>./mvnw spring-boot:run</code></li>
    </ol>

    <h2>🧪 Testing</h2>
    <p>We use Testcontainers to ensure our integration with Kafka and S3 is authentic.</p>
    <ul>
        <li><strong>Run all tests:</strong> <code>./mvnw test</code></li>
        <li><strong>Note:</strong> Tests require a running Docker daemon. They automatically spin up a Kafka broker and a LocalStack instance to emulate cloud infrastructure.</li>
    </ul>

    <h2>🔭 Observability</h2>
    <p>This service implements the three pillars of observability to ensure system health and rapid debugging.</p>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>Pillar</th>
            <th>Implementation</th>
            <th>Purpose</th>
        </tr>
        <tr>
            <td><strong>Logs</strong></td>
            <td>Structured (JSON) logs</td>
            <td>Centralized event tracking and error analysis.</td>
        </tr>
        <tr>
            <td><strong>Metrics</strong></td>
            <td>Spring Boot Actuator</td>
            <td>Tracking latency, request rates, and resource utilization.</td>
        </tr>
        <tr>
            <td><strong>Traces</strong></td>
            <td>OpenTelemetry / Tracing</td>
            <td>Visualizing request flow across the distributed system.</td>
        </tr>
    </table>

    <h3>Accessing Observability Tools</h3>
    <ul>
        <li><strong>Actuator Health:</strong> <code>GET /actuator/health</code></li>
        <li><strong>Metrics Dashboard:</strong> [Link to your Prometheus/Grafana dashboard]</li>
        <li><strong>Distributed Traces:</strong> [Link to your Jaeger/Zipkin UI]</li>
    </ul>

    <h2>⚙️ Configuration</h2>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>Variable</th>
            <th>Default</th>
            <th>Description</th>
        </tr>
        <tr>
            <td><code>AWS_S3_ENDPOINT</code></td>
            <td><code>http://localhost:9000</code></td>
            <td>S3 compatible endpoint</td>
        </tr>
        <tr>
            <td><code>KAFKA_BOOTSTRAP_SERVERS</code></td>
            <td><code>localhost:9092</code></td>
            <td>Kafka broker address</td>
        </tr>
        <tr>
            <td><code>AWS_S3_PRESIGNED_DURATION</code></td>
            <td><code>15</code></td>
            <td>Minutes for presigned URL validity</td>
        </tr>
    </table>

</body>
</html>