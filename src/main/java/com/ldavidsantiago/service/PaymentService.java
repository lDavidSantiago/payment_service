package com.ldavidsantiago.service;

import com.ldavidsantiago.entity.Payment;
import com.ldavidsantiago.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ServerProperties serverProperties;
    public Payment doPayment(Payment payment){
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
