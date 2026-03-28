package com.payflow.service;

import com.payflow.dto.PaymentDto;
import com.payflow.exception.PaymentNotFoundException;
import com.payflow.kafka.PaymentEventProducer;
import com.payflow.kafka.dto.PaymentEventDto;
import com.payflow.model.Payment;
import com.payflow.model.PaymentStatus;
import com.payflow.repository.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor

public class PaymentService {

  private final PaymentEventProducer paymentEventProducer;

  private final PaymentRepository paymentRepository;

  @Transactional
    public PaymentDto.Response createPayment(PaymentDto.CreateRequest request){
      log.info("Creating payment from sender: {}",request.getSenderId());

      Payment payment=Payment.builder()
              .senderId(request.getSenderId())
              .receiverId(request.getReceiverId())
              .amount(request.getAmount())
              .currency(request.getCurrency().toUpperCase())
              .description(request.getDescription())
              .status(PaymentStatus.PENDING)
              .build();

      Payment saved=paymentRepository.save(payment);

    paymentEventProducer.publishPaymentCreated(PaymentEventDto.builder()
            .paymentId(saved.getId())
            .senderId(saved.getSenderId())
            .receiverId(saved.getReceiverId())
            .amount(saved.getAmount())
            .currency(saved.getCurrency())
            .status(saved.getStatus())
            .eventType("PAYMENT_CREATED")
            .occurredAt(saved.getCreatedAt())
            .build());


    log.info("Payment created with id: {}", saved.getId());
      return toResponse(saved);
  }

    @Transactional(readOnly=true)
    public List<PaymentDto.Response> getAllPayments(){
      log.debug("Retrieving all payments");
      return paymentRepository.findAll()
              .stream()
              .map(this::toResponse)
              .collect(Collectors.toList());
    }
    private PaymentDto.Response toResponse(Payment payment) {
    return PaymentDto.Response.builder()
            .id(payment.getId())
            .senderId(payment.getSenderId())
            .receiverId(payment.getReceiverId())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .status(payment.getStatus())
            .description(payment.getDescription())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .build();
  }

  @Transactional(readOnly = true)
  public PaymentDto.Response getPaymentById(Long id){
    log.debug("Retrieving payment by id {}", id);
    Payment payment=paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException(id));
    return toResponse(payment);
  }
  @Transactional
  public PaymentDto.Response updatePaymentStatus(Long id, PaymentDto.StatusUpdateRequest request){
    log.info("Updating payment {} status from sender: {}",id,request.getStatus());
    Payment payment=paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException(id));
    payment.setStatus(request.getStatus());
    Payment updated=paymentRepository.save(payment);

    paymentEventProducer.publishPaymentStatusUpdated(PaymentEventDto.builder()
            .paymentId(updated.getId())
            .senderId(updated.getSenderId())
            .receiverId(updated.getReceiverId())
            .amount(updated.getAmount())
            .currency(updated.getCurrency())
            .status(updated.getStatus())
            .eventType("PAYMENT_STATUS_UPDATED")
            .occurredAt(updated.getUpdatedAt())
            .build());

    return toResponse(updated);
  }

  @Transactional
  public void deletePayment(Long id){
    log.info("Deleting payment by id {}", id);
    if(!paymentRepository.existsById(id)){
      throw new PaymentNotFoundException(id);
    }
    paymentRepository.deleteById(id);
  }


}
