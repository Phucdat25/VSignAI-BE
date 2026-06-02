package com.vsignai.backend.controller;

import com.vsignai.backend.dto.CreateFeedbackRequest;
import com.vsignai.backend.dto.response.ApiResponse;
import com.vsignai.backend.dto.response.PaginationResponseDTO;
import com.vsignai.backend.dto.response.TranslationDetailResponse;
import com.vsignai.backend.dto.response.TranslationHistoryResponse;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.service.FeedbackService;
import com.vsignai.backend.service.TranslationSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationSessionService translationSessionService;

    private final FeedbackService feedbackService;

    @GetMapping("/me")
    public ResponseEntity<
            ApiResponse<
                    PaginationResponseDTO<
                            List<TranslationHistoryResponse>
                            >
                    >
            > getMyHistory(

            @AuthenticationPrincipal User user,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy lịch sử dịch thành công",
                        translationSessionService.getMyHistory(
                                user,
                                page,
                                size
                        )
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<TranslationDetailResponse>
            > getTranslationDetail(

            @PathVariable
            Long id,

            @AuthenticationPrincipal
            User user
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy chi tiết bản dịch thành công",
                        translationSessionService.getDetail(
                                id,
                                user
                        )
                )
        );
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<ApiResponse<Void>>
    createFeedback(

            @PathVariable
            Long id,

            @Valid
            @RequestBody
            CreateFeedbackRequest request,

            @AuthenticationPrincipal
            User user
    ) {

        feedbackService.createFeedback(
                id,
                request,
                user
        );

        return ResponseEntity.ok(
                ApiResponse.successMessage(
                        "Gửi feedback thành công"
                )
        );
    }
}