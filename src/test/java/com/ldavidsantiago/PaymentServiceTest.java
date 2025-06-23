package com.ldavidsantiago;

import com.ldavidsantiago.entity.Payment;
import com.ldavidsantiago.repository.PaymentRepository;
import com.ldavidsantiago.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ServerProperties serverProperties;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoPayment_BookingExists() {
        Payment payment = new Payment();
        payment.setOrderId(1);
        payment.setAmount(100.0);
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(new Object(), HttpStatus.OK));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        Payment result = paymentService.doPayment(payment);
        assertNotNull(result.getTransactionId());
        assertNotNull(result.getPaymentStatus());
    }

    @Test
    void testDoPayment_BookingDoesNotExist() {
        Payment payment = new Payment();
        payment.setOrderId(999);
        payment.setAmount(100.0);
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenThrow(new RuntimeException("Booking no encontrado, no se puede procesar el pago"));
        Exception exception = assertThrows(RuntimeException.class, () -> paymentService.doPayment(payment));
        assertTrue(exception.getMessage().contains("Booking no encontrado"));
    }
} 