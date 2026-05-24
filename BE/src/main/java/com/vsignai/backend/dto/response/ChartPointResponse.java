package com.vsignai.backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartPointResponse {

    private String label;
    private Long value;
}