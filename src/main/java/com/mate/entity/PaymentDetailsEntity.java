package com.mate.entity;

import com.mate.entity.enums.Status;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document(collection = "payment")
@Data
public class PaymentDetailsEntity {
    @Id
    private String paymentId;
    private String senderId;
    private String bookingId;
    private Double amount;
    private Timestamp timestamp;
    private Status paymentStatus;
}
