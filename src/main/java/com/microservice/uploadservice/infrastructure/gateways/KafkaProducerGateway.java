package com.microservice.uploadservice.infrastructure.gateways;

import com.microservice.uploadservice.application.exceptions.KafkaServerException;
import com.microservice.uploadservice.application.gateways.MessageProducer;
import com.microservice.uploadservice.domain.Video;
import com.microservice.uploadservice.infrastructure.gateways.payload.VideoEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducerGateway implements MessageProducer {

    private final KafkaTemplate<String, VideoEventPayload> kafkaTemplate;

    @Value("${kafka.topic.video-uploaded}")
    private String topic;

    @Override
    public void sendEvent(Video video) {
        try {
            var payload = VideoEventPayload.builder()
                    .id(video.getId())
                    .title(video.getTitle())
                    .description(video.getDescription())
                    .author(video.getAuthor())
                    .duration(video.getDuration())
                    .size(video.getSize())
                    .videoStatus(video.getVideoStatus())
                    .build();

            kafkaTemplate.send(topic, video.getId().toString(), payload);
        } catch (KafkaException ex) {
            throw new KafkaServerException("Error while sending to kafka: " + ex.toString());
        }
    }
}
