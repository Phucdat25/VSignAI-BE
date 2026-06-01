package com.vsignai.backend.dto.response;

import com.vsignai.backend.enums.feedback.FeedbackCategory;
import com.vsignai.backend.enums.feedback.FeedbackStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FeedbackResponse {

    private Long id;

    private Long translationSessionId;

    private FeedbackCategory category;

    private String feedbackContent;

    private FeedbackStatus status;

    private String adminNote;

    private LocalDateTime createdAt;
}