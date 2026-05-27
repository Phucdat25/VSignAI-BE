package com.vsignai.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitRequest {
    private String packageType; // "PRO_MONTH" or "PRO_YEAR"
}
