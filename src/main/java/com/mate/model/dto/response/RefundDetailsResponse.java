package com.mate.model.dto.response;

import com.mate.model.RefundPolicy;
import lombok.Data;

@Data
public class RefundDetailsResponse {
    private Double refundAmount;
    private RefundPolicy refundPolicy;
}
