package com.payflow.kafka;

import com.payflow.kafka.config.KafkaTopicConfig;
import com.payflow.kafka.dto.PaymentEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentEventConsumer {

    @KafkaListener(
            topics = KafkaTopicConfig.PAYMENT_CREATED_TOPIC,
            groupId = "payflow-group"
    )
    public void onPaymentCreated(PaymentEventDto event) {
        log.info("[CONSUMER] Payment created → id={}, sender={}, amount={} {}",
                event.getPaymentId(),
                event.getSenderId(),
                event.getAmount(),
                event.getCurrency());
        // TODO: plug in notification service, fraud detection, audit log, etc.
    }

    @KafkaListener(
            topics = KafkaTopicConfig.PAYMENT_UPDATED_TOPIC,
            groupId = "payflow-group"
    )
    public void onPaymentStatusUpdated(PaymentEventDto event) {
        log.info("[CONSUMER] Payment status updated → id={}, status={}",
                event.getPaymentId(),
                event.getStatus());
        // TODO: trigger downstream processing per status
    }
}