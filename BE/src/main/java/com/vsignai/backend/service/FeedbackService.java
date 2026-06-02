package com.vsignai.backend.service;

import com.vsignai.backend.dto.CreateFeedbackRequest;
import com.vsignai.backend.dto.UpdateFeedbackStatusRequest;
import com.vsignai.backend.dto.response.AdminFeedbackResponse;
import com.vsignai.backend.dto.response.FeedbackResponse;
import com.vsignai.backend.dto.response.PaginationResponseDTO;
import com.vsignai.backend.entity.Feedback;
import com.vsignai.backend.entity.User;

import java.util.List;

public interface FeedbackService {

    Feedback createFeedback(
            Long translationSessionId,
            CreateFeedbackRequest request,
            User user
    );

    PaginationResponseDTO<List<FeedbackResponse>>
    getMyFeedbacks(
            User user,
            int page,
            int size
    );

    PaginationResponseDTO<List<AdminFeedbackResponse>>
    getAllFeedbacks(
            int page,
            int size
    );

    void updateFeedbackStatus(
            Long feedbackId,
            UpdateFeedbackStatusRequest request
    );
}