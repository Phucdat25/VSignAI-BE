package com.vsignai.backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SignVideoUploadRequest {

    private String keyword;

    private MultipartFile file;
}
