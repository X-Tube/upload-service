package com.microservice.uploadservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.uploadservice.application.exceptions.KafkaServerException;
import com.microservice.uploadservice.domain.Video;
import com.microservice.uploadservice.domain.enums.VideoStatus;
import com.microservice.uploadservice.infrastructure.gateways.KafkaProducerGateway;
import com.microservice.uploadservice.infrastructure.gateways.payload.VideoEventPayload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

@Slf4j
@Import(KafkaProducerGatewayIntegrationTest.KafkaTestConsumerConfig.class)
class KafkaProducerGatewayIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private KafkaProducerGateway kafkaProducerGateway;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private KafkaTemplate<String, VideoEventPayload> kafkaTemplate;

    public static final BlockingQueue<String> testQueue = new LinkedBlockingQueue<>();

    @BeforeEach
    void setUp() {
        testQueue.clear();
    }

    @Test
    void shouldProduceMessageToKafkaContainerSuccessfully() throws Exception {
        Video video = Video.builder()
                .id(UUID.randomUUID())
                .title("Microservices with Go and Java")
                .description("Streaming platform architecture")
                .author(1L)
                .duration(240L)
                .size(1024000L)
                .videoStatus(VideoStatus.PROCESSING)
                .build();

        log.info("[TEST] Sending video event to Kafka container. Video ID: {}", video.getId());

        assertDoesNotThrow(() -> kafkaProducerGateway.sendEvent(video));

        String rawKafkaPayload = testQueue.poll(5, TimeUnit.SECONDS);

        assertNotNull(rawKafkaPayload, "Did not receive a message from Kafka within the timeout!");
        log.info("[TEST] Received payload from Kafka broker: {}", rawKafkaPayload);

        VideoEventPayload receivedPayload = objectMapper.readValue(rawKafkaPayload, VideoEventPayload.class);

        assertEquals(video.getId(), receivedPayload.id());
        assertEquals(video.getTitle(), receivedPayload.title());
        assertEquals(video.getAuthor(), receivedPayload.author());
    }

    @Test
    void shouldThrowKafkaServerExceptionWhenBrokerFails() {
        Video video = Video.builder()
                .id(UUID.randomUUID())
                .title("Chaos Engineering Test")
                .author(1L)
                .build();

        doThrow(new KafkaException("Simulated Network Timeout from Testcontainers"))
                .when(kafkaTemplate).send(anyString(), anyString(), any(VideoEventPayload.class));

        KafkaServerException exception = assertThrows(KafkaServerException.class, () -> {
            kafkaProducerGateway.sendEvent(video);
        });

        assertTrue(exception.getMessage().contains("Error while sending to kafka"));
        log.info("[TEST] Successfully caught expected exception: {}", exception.getMessage());
    }

    @TestConfiguration
    static class KafkaTestConsumerConfig {

        @KafkaListener(topics = "${kafka.topic.video-uploaded}", groupId = "integration-test-group")
        public void listen(String payloadRaw) {
            testQueue.add(payloadRaw);
        }
    }
}