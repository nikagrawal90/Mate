package com.mate.entity;

import com.mate.entity.enums.Status;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document(collection = "booking")
@Data
public class BookingEntity {
    @Id
    private String bookingId;
    private String eventId;
    private String userId;
    private String paymentId;
    private Status status;
    private Timestamp bookingTimestamp;
}