package com.vsignai.backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyActivityResponse {
    private String label;
    private Long activeUsers;
    private Long translations;
}
