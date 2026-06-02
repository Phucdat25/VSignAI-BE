package com.vsignai.backend.dto;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.enums.translation.TranslationStatus;
import com.vsignai.backend.enums.translation.TranslationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateTranslationSessionRequest {

    private User user;

    private TranslationType translationType;

    private TranslationStatus status;

    private String sourceUrl;

    private String resultUrl;

    private String inputContent;

    private String outputContent;

    private Long processingTimeMs;

    private String aiVersion;

    private String errorMessage;
}