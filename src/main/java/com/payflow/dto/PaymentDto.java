package com.payflow.dto;

import com.payflow.model.PaymentStatus;
import jakarta.validation.constraints.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *  NOTE: Why DTOs (Data Transfer Objects)?
 *
 * Never expose your entity directly in  API.
 * Reasons:
 * 1.  entity might have sensitive fields you don't want to expose
 * 2. API shape and DB shape should be independent - they change for different reasons
 * 3.  can validate incoming data before it touches entity
 *
 * Rule of thumb:
 * - Request DTO = what the CLIENT sends TO you
 * - Response DTO = what YOU send BACK to the client
 */
public class PaymentDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest{ //← what client sends

    @NotBlank(message="Sender ID is required")
        private String senderId;
    @NotBlank(message = "Receiver ID is required")
        private String receiverId;
    @NotNull(message="Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than  0")
        private BigDecimal amount;
    @NotBlank(message="Currency is required")
    @Size(min=3,max=3,message="Currency must be 3 letters e.g. CAD")
        private String currency;

        private  String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private Long id;
        private String senderId;
        private String receiverId;
        private BigDecimal amount;
        private String currency;
        private PaymentStatus status;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest{
        @NotNull(message="Status is required")
        private PaymentStatus status;
    }

}
