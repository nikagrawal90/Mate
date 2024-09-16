package com.mate.model.dto.request;

import com.mate.entity.enums.Status;
import lombok.Data;

@Data
public class PaymentStatusNotificationRequest {
    private String paymentId;
    private Status paymentStatus;
}
