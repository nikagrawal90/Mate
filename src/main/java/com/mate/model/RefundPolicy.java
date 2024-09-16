package com.mate.model;

import lombok.Data;

import java.time.Duration;

@Data
public class RefundPolicy {
    private Double percentageRefundable;
    private Double AdditionalCharges;
    private Duration refundWindow;
}
