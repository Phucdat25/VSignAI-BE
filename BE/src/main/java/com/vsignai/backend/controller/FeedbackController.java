package com.vsignai.backend.controller;

import com.vsignai.backend.dto.response.ApiResponse;
import com.vsignai.backend.dto.response.FeedbackResponse;
import com.vsignai.backend.dto.response.PaginationResponseDTO;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/me")
    public ResponseEntity<
            ApiResponse<
                    PaginationResponseDTO<
                            List<FeedbackResponse>
                            >
                    >
            > getMyFeedbacks(

            @AuthenticationPrincipal
            User user,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách feedback thành công",
                        feedbackService.getMyFeedbacks(
                                user,
                                page,
                                size
                        )
                )
        );
    }
}