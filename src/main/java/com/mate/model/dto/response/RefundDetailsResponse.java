package com.mate.model.dto.response;

import com.mate.model.RefundPolicy;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundDetailsResponse {
    private Double refundAmount;
    private RefundPolicy refundPolicy;
}
