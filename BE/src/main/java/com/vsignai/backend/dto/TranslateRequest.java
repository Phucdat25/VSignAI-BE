package com.vsignai.backend.dto;

import lombok.Data;

@Data
public class TranslateRequest {

    private String text;

    // FE gửi số giây sử dụng
    private Integer usedSeconds;
}
