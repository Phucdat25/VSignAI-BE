package com.vsignai.backend.service;

import com.vsignai.backend.dto.CreateTranslationSessionRequest;
import com.vsignai.backend.dto.response.PaginationResponseDTO;
import com.vsignai.backend.dto.response.TranslationDetailResponse;
import com.vsignai.backend.dto.response.TranslationHistoryResponse;
import com.vsignai.backend.entity.TranslationSession;
import com.vsignai.backend.entity.User;


import java.util.List;

public interface TranslationSessionService {

    TranslationSession createSession(
            CreateTranslationSessionRequest request
    );

    PaginationResponseDTO<List<TranslationHistoryResponse>>
    getMyHistory(
            User user,
            int page,
            int size
    );

    TranslationDetailResponse getDetail(
            Long sessionId,
            User user
    );
}