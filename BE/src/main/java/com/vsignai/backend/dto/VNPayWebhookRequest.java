package com.vsignai.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VNPayWebhookRequest {

    private String transactionId;
    private String status;
    private String amount;

    public String toRawString() {
        return transactionId + "|" + status + "|" + amount;
    }
}
