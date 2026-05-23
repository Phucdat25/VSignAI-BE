package com.vsignai.backend.controller;

import com.vsignai.backend.dto.response.SignVideoResponse;
import com.vsignai.backend.entity.SignVideo;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.service.SignVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/sign-videos")
@RequiredArgsConstructor
public class SignVideoController {

    private final SignVideoService signVideoService;
    private final UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<SignVideo> upload(

            @RequestParam String keyword,

            @RequestParam MultipartFile file
    ) {

        return ResponseEntity.ok(
                signVideoService.upload(keyword, file)
        );
    }

//    @GetMapping("/{keyword}")
//    public ResponseEntity<SignVideoResponse> get(
//            @PathVariable String keyword
//    ) {
//
//        return ResponseEntity.ok(
//                signVideoService.getByKeyword(keyword)
//        );
//    }
@GetMapping("/{keyword}")
public ResponseEntity<SignVideoResponse> get(
        @PathVariable String keyword,
        Authentication authentication
) {

    User user = (User) authentication.getPrincipal();

    return ResponseEntity.ok(
            signVideoService.getByKeyword(
                    keyword,
                    user
            )
    );
}
}