package com.payflow.kafka.dto;

import com.payflow.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventDto {

    private Long       paymentId;
    private String     senderId;
    private String     receiverId;
    private BigDecimal amount;
    private String     currency;
    private PaymentStatus status;
    private String     eventType;   // "PAYMENT_CREATED" | "PAYMENT_STATUS_UPDATED"
    private LocalDateTime occurredAt;
}