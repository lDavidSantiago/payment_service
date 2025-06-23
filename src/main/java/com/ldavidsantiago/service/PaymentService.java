package com.ldavidsantiago.service;

import com.ldavidsantiago.entity.Payment;
import com.ldavidsantiago.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ServerProperties serverProperties;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${microservice.booking-service.url}")
    private String bookingServiceUrl;

    public Payment doPayment(Payment payment){
        // Verificar existencia del booking antes de procesar el pago
        String url = bookingServiceUrl + "/book/" + payment.getOrderId();
        try {
            ResponseEntity<?> response = restTemplate.getForEntity(url, Object.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Booking no encontrado, no se puede procesar el pago");
            }
        } catch (Exception e) {
            throw new RuntimeException("Booking no encontrado, no se puede procesar el pago");
        }
        payment.setPaymentStatus(paymentProcessing());
        payment.setTransactionId(UUID.randomUUID().toString());
        Integer port = serverProperties.getPort();
        log.info("Request servered by port :{}",port);
        return paymentRepository.save(payment);
    }

    private String paymentProcessing() {
        return new Random().nextBoolean()?"Success":"Failure";
    }
}
