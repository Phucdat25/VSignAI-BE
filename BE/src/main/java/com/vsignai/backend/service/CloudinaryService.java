package com.vsignai.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    public Map uploadVideo(MultipartFile file);
}
