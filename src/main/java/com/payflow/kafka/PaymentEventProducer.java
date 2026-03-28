package com.payflow.kafka;

import com.payflow.kafka.config.KafkaTopicConfig;
import com.payflow.kafka.dto.PaymentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentEventDto> kafkaTemplate;

    public void publishPaymentCreated(PaymentEventDto event) {
        publish(KafkaTopicConfig.PAYMENT_CREATED_TOPIC, event);
    }

    public void publishPaymentStatusUpdated(PaymentEventDto event) {
        publish(KafkaTopicConfig.PAYMENT_UPDATED_TOPIC, event);
    }

    private void publish(String topic, PaymentEventDto event) {
        // Key = paymentId.toString() → ensures all events for the same payment
        // go to the same partition (ordering guarantee per payment)
        String key = event.getPaymentId().toString();

        CompletableFuture<SendResult<String, PaymentEventDto>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event [{}] for paymentId={}: {}",
                        event.getEventType(), event.getPaymentId(), ex.getMessage());
            } else {
                log.info("Published event [{}] for paymentId={} → topic={}, partition={}, offset={}",
                        event.getEventType(),
                        event.getPaymentId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}