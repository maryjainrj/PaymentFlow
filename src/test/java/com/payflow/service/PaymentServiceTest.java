package com.payflow.service;

import com.payflow.dto.PaymentDto;
import com.payflow.exception.PaymentNotFoundException;
import com.payflow.kafka.PaymentEventProducer;
import com.payflow.model.Payment;
import com.payflow.model.PaymentStatus;
import com.payflow.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentDto.CreateRequest createRequest;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(1L)
                .senderId("sender@test.com")
                .receiverId("receiver@test.com")
                .amount(new BigDecimal("100.00"))
                .currency("CAD")
                .status(PaymentStatus.PENDING)
                .description("Test payment")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new PaymentDto.CreateRequest(
                "sender@test.com",
                "receiver@test.com",
                new BigDecimal("100.00"),
                "CAD",
                "Test payment"
        );
    }

    @Test
    void createPayment_success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        doNothing().when(paymentEventProducer).publishPaymentCreated(any());

        PaymentDto.Response response = paymentService.createPayment(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.getSenderId()).isEqualTo("sender@test.com");
        assertThat(response.getAmount()).isEqualByComparingTo("100.00");
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(paymentEventProducer, times(1)).publishPaymentCreated(any());
    }

    @Test
    void createPayment_currencyIsUppercased() {
        createRequest = new PaymentDto.CreateRequest(
                "sender@test.com",
                "receiver@test.com",
                new BigDecimal("100.00"),
                "cad",
                "Test"
        );
        Payment savedPayment = Payment.builder()
                .id(1L)
                .senderId("sender@test.com")
                .receiverId("receiver@test.com")
                .amount(new BigDecimal("100.00"))
                .currency("CAD")
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        doNothing().when(paymentEventProducer).publishPaymentCreated(any());

        PaymentDto.Response response = paymentService.createPayment(createRequest);

        assertThat(response.getCurrency()).isEqualTo("CAD");
    }

    @Test
    void getAllPayments_returnsAllPayments() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<PaymentDto.Response> responses = paymentService.getAllPayments();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void getPaymentById_success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentDto.Response response = paymentService.getPaymentById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSenderId()).isEqualTo("sender@test.com");
    }

    @Test
    void getPaymentById_throwsException_whenNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentById(99L))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updatePaymentStatus_success() {
        PaymentDto.StatusUpdateRequest statusRequest =
                new PaymentDto.StatusUpdateRequest(PaymentStatus.COMPLETED);

        Payment updatedPayment = Payment.builder()
                .id(1L)
                .senderId("sender@test.com")
                .receiverId("receiver@test.com")
                .amount(new BigDecimal("100.00"))
                .currency("CAD")
                .status(PaymentStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
        doNothing().when(paymentEventProducer).publishPaymentStatusUpdated(any());

        PaymentDto.Response response = paymentService.updatePaymentStatus(1L, statusRequest);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        verify(paymentEventProducer, times(1)).publishPaymentStatusUpdated(any());
    }

    @Test
    void updatePaymentStatus_throwsException_whenNotFound() {
        PaymentDto.StatusUpdateRequest statusRequest =
                new PaymentDto.StatusUpdateRequest(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.updatePaymentStatus(99L, statusRequest))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deletePayment_throwsException_whenNotFound() {
        when(paymentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.deletePayment(99L))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deletePayment_success() {
        when(paymentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(paymentRepository).deleteById(1L);

        paymentService.deletePayment(1L);

        verify(paymentRepository, times(1)).deleteById(1L);
    }
}