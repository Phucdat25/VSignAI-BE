package com.vsignai.backend.dto;

import com.vsignai.backend.enums.feedback.FeedbackCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFeedbackRequest {

    @NotNull(message = "Loại feedback không được để trống")
    private FeedbackCategory category;

    @NotBlank(message = "Nội dung feedback không được để trống")
    @Size(
            max = 2000,
            message = "Feedback tối đa 2000 ký tự"
    )
    private String feedbackContent;
}