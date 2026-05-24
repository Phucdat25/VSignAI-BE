package com.vsignai.backend.controller;

import com.vsignai.backend.dto.common.ApiResponse;
import com.vsignai.backend.dto.response.SignVideoResponse;
import com.vsignai.backend.entity.SignVideo;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.security.UserPrincipal;
import com.vsignai.backend.service.SignVideoService;
import com.vsignai.backend.service.UserService;
import com.vsignai.backend.util.ResponseFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/sign-videos")
@RequiredArgsConstructor
public class SignVideoController {

    private final SignVideoService signVideoService;
    private final UserService userService;

    @PostMapping("/upload")
    public ApiResponse<SignVideo> upload(
            @RequestParam String keyword,
            @RequestParam MultipartFile file
    ) {

        return ResponseFactory.success(
                signVideoService.upload(keyword, file),
                "Upload sign video successfully"
        );
    }

    @GetMapping("/{keyword}")
    public ApiResponse<SignVideoResponse> get(
            @PathVariable String keyword,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        User user = principal.getUser();

        return ResponseFactory.success(
                signVideoService.getByKeyword(keyword, user),
                "Get sign video successfully"
        );
    }
}