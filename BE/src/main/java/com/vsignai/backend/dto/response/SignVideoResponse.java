package com.vsignai.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignVideoResponse {

    private String keyword;

    private String videoUrl;

    private String thumbnailUrl;
}
