package com.vsignai.backend.service;

import com.vsignai.backend.dto.response.AiPredictResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AiService {

    AiPredictResponse predict(MultipartFile file, Integer durationSeconds);
}
