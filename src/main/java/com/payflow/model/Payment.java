package com.payflow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payments")
@Data //for getters setters lombok
@Builder //lets you use Payment.builder().amount(...).build() lombok
@NoArgsConstructor //generates empty constructor (required by JPA) lombok
@AllArgsConstructor //generates constructor with all fields lombok

public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String receiverId;

    @Column(nullable = false,precision=19,scale=4)
    private BigDecimal amount;

    @Column(nullable = false,length=3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // @PrePersist runs automatically before saving a new record
    @PrePersist
    protected void onCreate(){
        createdAt =LocalDateTime.now();
        updatedAt =LocalDateTime.now();
        if(status==null){
            status=PaymentStatus.PENDING;

        }
    }
    //  @PreUpdate runs automatically before updating an existing record
    @PreUpdate
    protected void onUpdate(){
        updatedAt =LocalDateTime.now();
    }
}
