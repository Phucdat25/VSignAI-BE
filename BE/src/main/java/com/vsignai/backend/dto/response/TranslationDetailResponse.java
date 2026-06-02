package com.vsignai.backend.dto.response;

import com.vsignai.backend.enums.translation.TranslationStatus;
import com.vsignai.backend.enums.translation.TranslationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TranslationDetailResponse {

    private Long id;

    private TranslationType translationType;

    private TranslationStatus status;

    private String sourceUrl;

    private String resultUrl;

    private String inputContent;

    private String outputContent;

    private Long processingTimeMs;

    private String aiVersion;

    private String errorMessage;

    private LocalDateTime createdAt;
}