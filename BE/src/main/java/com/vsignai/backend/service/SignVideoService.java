package com.vsignai.backend.service;

import com.vsignai.backend.dto.response.SignVideoResponse;
import com.vsignai.backend.entity.SignVideo;
import org.springframework.web.multipart.MultipartFile;

public interface SignVideoService {
    public SignVideo upload(String keyword, MultipartFile file);
    public SignVideoResponse getByKeyword(String keyword);
}
