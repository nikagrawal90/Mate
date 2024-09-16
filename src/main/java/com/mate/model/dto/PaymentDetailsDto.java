package com.mate.model.dto;

import com.mate.entity.enums.Status;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaymentDetailsDto {
    private String paymentId;
    private String senderId;
    private String bookingId;
    private Double amount;
    private Timestamp timestamp;
    private Status paymentStatus;
}
