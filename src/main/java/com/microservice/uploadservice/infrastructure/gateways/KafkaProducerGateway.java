package com.microservice.uploadservice.infrastructure.gateways;

import com.microservice.uploadservice.application.exceptions.KafkaServerException;
import com.microservice.uploadservice.application.gateways.MessageProducer;
import com.microservice.uploadservice.domain.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducerGateway implements MessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.video-uploaded}")
    private String topic;

    @Override
    public void sendEvent(Video video) {
        try {
            kafkaTemplate.send(topic, video.getId().toString(), video);
        } catch (KafkaException ex) {
            throw new KafkaServerException("Error while sending to kafka: " + ex.toString());
        }
    }
}
