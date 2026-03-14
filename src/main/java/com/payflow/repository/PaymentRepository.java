package com.payflow.repository;

import com.payflow.model.Payment;
import com.payflow.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
   List<Payment> findBySenderId(String senderId);
   List<Payment> findByReceiverId(String receiverId);
   List<Payment> findByStatus(PaymentStatus status);
   List<Payment> findBySenderIdAndStatus(String senderId, PaymentStatus status);
}
