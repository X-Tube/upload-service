package com.microservice.uploadservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
    );

    static final LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.4.0")
    ).withServices(LocalStackContainer.Service.S3);

    static {
        kafka.start();
        localstack.start();

        try {
            localstack.execInContainer("awslocal", "s3", "mb", "s3://test-video-bucket");
            localstack.execInContainer("awslocal", "s3", "mb", "s3://test-thumbnail-bucket");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize LocalStack S3 buckets", e);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("kafka.topic.video-uploaded", () -> "test-video-uploaded-topic");

        registry.add("aws.s3.endpoint", () -> localstack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("aws.s3.region", localstack::getRegion);
        registry.add("aws.s3.access-key", localstack::getAccessKey);
        registry.add("aws.s3.secret-key", localstack::getSecretKey);

        registry.add("aws.s3.buckets.video-input", () -> "test-video-bucket");
        registry.add("aws.s3.buckets.thumbnail-input", () -> "test-thumbnail-bucket");
        registry.add("aws.s3.buckets.duration", () -> 15);
    }
}