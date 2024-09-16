package com.mate.model.dto;

import com.mate.entity.enums.Status;
import lombok.Data;

@Data
public class BookingDto {
    private String bookingId;
    private String eventId;
    private String userId;
    private String paymentId;
    private Double bookingAmount;
    private Status status;
}
