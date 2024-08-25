package com.mate.model.dto.request;

import lombok.Data;

@Data
public class ConfirmBookingRequest {
    private String bookingId;
    private String paymentId;
}
