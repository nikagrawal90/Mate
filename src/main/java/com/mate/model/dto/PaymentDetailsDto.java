package com.mate.model.dto;

import lombok.Data;

@Data
public class PaymentDetailsDto {
    private Double amount;
    private String paymentId;
    private String userId;
    private String eventId;
}
