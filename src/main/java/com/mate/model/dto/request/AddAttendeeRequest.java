package com.mate.model.dto.request;

import com.mate.model.dto.PaymentDetailsDto;
import lombok.Data;

@Data
public class AddAttendeeRequest {
    private String attendeeId;
    private String eventId;
    private PaymentDetailsDto paymentDetails;
}
