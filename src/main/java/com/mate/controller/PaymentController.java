package com.mate.controller;

import com.mate.entity.PaymentDetailsEntity;
import com.mate.model.dto.request.PaymentStatusNotificationRequest;
import com.mate.service.PaymentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@CrossOrigin("*")
@Log4j2
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<String> paymentStatusWebhook(PaymentStatusNotificationRequest paymentStatusNotificationRequest) {
        // verify from Payment Gateway if verification fails publish a metric

        try {
            PaymentDetailsEntity paymentDetails = paymentService.getPaymentDetailsById(paymentStatusNotificationRequest.getPaymentId());
            paymentService.updatePaymentStatus(paymentDetails, paymentStatusNotificationRequest.getPaymentStatus());
            return ResponseEntity.ok("Successfully updated payment Status");
        } catch (Exception e) {
            log.error("Error updating payment status");
            return ResponseEntity.internalServerError().body(e.getMessage());
        }


        // On failure write to DLQ for retry
    }
}
