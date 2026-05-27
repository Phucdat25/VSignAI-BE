package com.vsignai.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VNPayIpnRequest {

    private String vnp_TxnRef;

    private String vnp_TransactionNo;

    private String vnp_ResponseCode;

    private String vnp_Amount;

    private String vnp_SecureHash;
}