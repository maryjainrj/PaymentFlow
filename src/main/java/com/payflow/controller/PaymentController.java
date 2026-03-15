package com.payflow.controller;


import com.payflow.dto.PaymentDto;
import com.payflow.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDto.Response> createPayment(
       @Valid @RequestBody PaymentDto.CreateRequest request
    ){
        log.info("POST /api/v1/payments");
        PaymentDto.Response response=paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<PaymentDto.Response>> getAllPayment(){
        log.info("GET /api/v1/payments");
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto.Response> getPaymentById(@PathVariable Long id){
        log.info("GET /api/v1/payments/{}",id);
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
    @PatchMapping("/{id}/status")
        public ResponseEntity<PaymentDto.Response> updatePaymentStatus(
                @PathVariable Long id,
                @Valid @RequestBody PaymentDto.StatusUpdateRequest request){
                    log.info("PATCH /api/v1/payments/{}/status", id);
                    return ResponseEntity.ok(paymentService.updatePaymentStatus(id,request));
        }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id){
    log.info("DELETE /api/v1/payments/{}",id);
    paymentService.deletePayment(id);
    return ResponseEntity.noContent().build();
    }
}



