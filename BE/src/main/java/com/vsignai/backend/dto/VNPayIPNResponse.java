package com.vsignai.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VNPayIPNResponse {

    @JsonProperty("RspCode")
    private String rspCode;

    @JsonProperty("Message")
    private String message;

    public static VNPayIPNResponse confirmSuccess() {
        return new VNPayIPNResponse("00", "Confirm Success");
    }

    public static VNPayIPNResponse orderNotFound() {
        return new VNPayIPNResponse("01", "Order not found");
    }

    public static VNPayIPNResponse orderAlreadyConfirmed() {
        return new VNPayIPNResponse("02", "Order already confirmed");
    }

    public static VNPayIPNResponse invalidAmount() {
        return new VNPayIPNResponse("04", "Invalid amount");
    }

    public static VNPayIPNResponse invalidChecksum() {
        return new VNPayIPNResponse("97", "Invalid Checksum");
    }

    public static VNPayIPNResponse unknownError() {
        return new VNPayIPNResponse("99", "Unknown error");
    }
}
