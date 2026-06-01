package com.vsignai.backend.dto;

import com.vsignai.backend.enums.feedback.FeedbackStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFeedbackStatusRequest {

    private FeedbackStatus status;

    private String adminNote;
}