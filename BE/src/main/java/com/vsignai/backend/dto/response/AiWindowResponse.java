package com.vsignai.backend.dto.response;

import lombok.Data;

@Data
public class AiWindowResponse {
    private Integer start;
    private Integer end;
    private String gloss;
    private Double confidence;
}
