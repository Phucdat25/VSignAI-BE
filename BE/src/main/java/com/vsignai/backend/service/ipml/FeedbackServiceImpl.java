package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.CreateFeedbackRequest;
import com.vsignai.backend.dto.UpdateFeedbackStatusRequest;
import com.vsignai.backend.dto.response.AdminFeedbackResponse;
import com.vsignai.backend.entity.Feedback;
import com.vsignai.backend.entity.TranslationSession;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.enums.feedback.FeedbackStatus;
import com.vsignai.backend.exception.AppException;
import com.vsignai.backend.repository.FeedbackRepository;
import com.vsignai.backend.repository.TranslationSessionRepository;
import com.vsignai.backend.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.vsignai.backend.dto.response.FeedbackResponse;
import com.vsignai.backend.dto.response.PaginationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl
        implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final TranslationSessionRepository translationSessionRepository;

    @Override
    public Feedback createFeedback(
            Long translationSessionId,
            CreateFeedbackRequest request,
            User user
    ) {

        TranslationSession session =
                translationSessionRepository
                        .findById(translationSessionId)
                        .orElseThrow(() ->
                                new AppException(
                                        HttpStatus.NOT_FOUND,
                                        "Không tìm thấy bản dịch"
                                )
                        );

        if (!session.getUser().getId().equals(user.getId())) {

            throw new AppException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền feedback bản dịch này"
            );
        }

        Feedback feedback =
                Feedback.builder()
                        .translationSession(session)
                        .user(user)
                        .category(request.getCategory())
                        .feedbackContent(
                                request.getFeedbackContent()
                        )
                        .status(FeedbackStatus.OPEN)
                        .build();

        return feedbackRepository.save(feedback);
    }


    @Override
    public PaginationResponseDTO<List<FeedbackResponse>>
    getMyFeedbacks(
            User user,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        Page<Feedback> feedbackPage =
                feedbackRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                user.getId(),
                                pageable
                        );

        List<FeedbackResponse> data =
                feedbackPage
                        .getContent()
                        .stream()
                        .map(feedback ->
                                FeedbackResponse.builder()
                                        .id(feedback.getId())
                                        .translationSessionId(
                                                feedback
                                                        .getTranslationSession()
                                                        .getId()
                                        )
                                        .category(
                                                feedback.getCategory()
                                        )
                                        .feedbackContent(
                                                feedback.getFeedbackContent()
                                        )
                                        .status(
                                                feedback.getStatus()
                                        )
                                        .adminNote(
                                                feedback.getAdminNote()
                                        )
                                        .createdAt(
                                                feedback.getCreatedAt()
                                        )
                                        .build()
                        )
                        .toList();

        return PaginationResponseDTO
                .<List<FeedbackResponse>>builder()
                .totalItems(
                        feedbackPage.getTotalElements()
                )
                .totalPages(
                        feedbackPage.getTotalPages()
                )
                .currentPage(
                        feedbackPage.getNumber()
                )
                .pageSize(
                        feedbackPage.getSize()
                )
                .data(data)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDTO<List<AdminFeedbackResponse>>
    getAllFeedbacks(
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Feedback> feedbackPage =
                feedbackRepository
                        .findAllByOrderByCreatedAtDesc(
                                pageable
                        );

        List<AdminFeedbackResponse> data =
                feedbackPage.getContent()
                        .stream()
                        .map(feedback ->
                                AdminFeedbackResponse.builder()
                                        .id(feedback.getId())
                                        .userId(
                                                feedback.getUser().getId()
                                        )
                                        .userEmail(
                                                feedback.getUser().getEmail()
                                        )
                                        .translationSessionId(
                                                feedback
                                                        .getTranslationSession()
                                                        .getId()
                                        )
                                        .category(
                                                feedback.getCategory()
                                        )
                                        .feedbackContent(
                                                feedback.getFeedbackContent()
                                        )
                                        .status(
                                                feedback.getStatus()
                                        )
                                        .adminNote(
                                                feedback.getAdminNote()
                                        )
                                        .createdAt(
                                                feedback.getCreatedAt()
                                        )
                                        .build()
                        )
                        .toList();

        return PaginationResponseDTO
                .<List<AdminFeedbackResponse>>builder()
                .totalItems(
                        feedbackPage.getTotalElements()
                )
                .totalPages(
                        feedbackPage.getTotalPages()
                )
                .currentPage(
                        feedbackPage.getNumber()
                )
                .pageSize(
                        feedbackPage.getSize()
                )
                .data(data)
                .build();
    }

    @Override
    public void updateFeedbackStatus(
            Long feedbackId,
            UpdateFeedbackStatusRequest request
    ) {

        Feedback feedback =
                feedbackRepository
                        .findById(feedbackId)
                        .orElseThrow(() ->
                                new AppException(
                                        HttpStatus.NOT_FOUND,
                                        "Không tìm thấy feedback"
                                )
                        );

        feedback.setStatus(
                request.getStatus()
        );

        feedback.setAdminNote(
                request.getAdminNote()
        );

        feedbackRepository.save(feedback);
    }
}
