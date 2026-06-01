package com.vsignai.backend.controller;

import com.vsignai.backend.dto.UpdateFeedbackStatusRequest;
import com.vsignai.backend.dto.response.AdminFeedbackResponse;
import com.vsignai.backend.dto.response.ApiResponse;
import com.vsignai.backend.dto.response.PaginationResponseDTO;
import com.vsignai.backend.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/feedbacks")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    PaginationResponseDTO<
                                                List<AdminFeedbackResponse>
                                                >
                    >
            > getAllFeedbacks(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách feedback thành công",
                        feedbackService.getAllFeedbacks(
                                page,
                                size
                        )
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>>
    updateStatus(

            @PathVariable
            Long id,

            @RequestBody
            UpdateFeedbackStatusRequest request
    ) {

        feedbackService.updateFeedbackStatus(
                id,
                request
        );

        return ResponseEntity.ok(
                ApiResponse.successMessage(
                        "Cập nhật feedback thành công"
                )
        );
    }
}
