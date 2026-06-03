package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.CreateTranslationSessionRequest;
import com.vsignai.backend.dto.response.PaginationResponseDTO;
import com.vsignai.backend.dto.response.TranslationDetailResponse;
import com.vsignai.backend.dto.response.TranslationHistoryResponse;
import com.vsignai.backend.entity.TranslationSession;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.exception.AppException;
import com.vsignai.backend.repository.TranslationSessionRepository;
import com.vsignai.backend.service.TranslationSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationSessionServiceImpl
        implements TranslationSessionService {

    private final TranslationSessionRepository repository;

    @Override
    public TranslationSession createSession(
            CreateTranslationSessionRequest request
    ) {

        TranslationSession session =
                TranslationSession.builder()
                        .user(request.getUser())
                        .translationType(request.getTranslationType())
                        .status(request.getStatus())
                        .sourceUrl(request.getSourceUrl())
                        .resultUrl(request.getResultUrl())
                        .inputContent(request.getInputContent())
                        .outputContent(request.getOutputContent())
                        .processingTimeMs(
                                request.getProcessingTimeMs()
                        )
                        .aiVersion(request.getAiVersion())
                        .errorMessage(
                                request.getErrorMessage()
                        )
                        .build();
        System.out.println("SAVE SESSION START");
        TranslationSession saved =
                repository.save(session);
        System.out.println("SAVE SESSION DONE = " + saved.getId());
        log.info(
                "Translation session saved. id={}, userId={}, type={}",
                saved.getId(),
                saved.getUser().getId(),
                saved.getTranslationType()
        );

        return saved;
    }

    @Override
    public PaginationResponseDTO<List<TranslationHistoryResponse>>
    getMyHistory(
            User user,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        Page<TranslationSession> sessionPage =
                repository.findByUserIdOrderByCreatedAtDesc(
                        user.getId(),
                        pageable
                );

        List<TranslationHistoryResponse> data =
                sessionPage
                        .getContent()
                        .stream()
                        .map(session ->
                                TranslationHistoryResponse
                                        .builder()
                                        .id(session.getId())
                                        .translationType(
                                                session.getTranslationType()
                                        )
                                        .status(
                                                session.getStatus()
                                        )
                                        .inputContent(
                                                session.getInputContent()
                                        )
                                        .outputContent(
                                                session.getOutputContent()
                                        )
                                        .sourceUrl(
                                                session.getSourceUrl()
                                        )
                                        .resultUrl(
                                                session.getResultUrl()
                                        )
                                        .createdAt(
                                                session.getCreatedAt()
                                        )
                                        .build()
                        )
                        .toList();

        return PaginationResponseDTO
                .<List<TranslationHistoryResponse>>builder()
                .totalItems(
                        sessionPage.getTotalElements()
                )
                .totalPages(
                        sessionPage.getTotalPages()
                )
                .currentPage(
                        sessionPage.getNumber()
                )
                .pageSize(
                        sessionPage.getSize()
                )
                .data(data)
                .build();
    }

    @Override
    public TranslationDetailResponse getDetail(
            Long sessionId,
            User user
    ) {

        TranslationSession session =
                repository.findById(sessionId)
                        .orElseThrow(() ->
                                new AppException(
                                        HttpStatus.NOT_FOUND,
                                        "Không tìm thấy lịch sử dịch"
                                )
                        );

        if (!session.getUser().getId().equals(user.getId())) {
            throw new AppException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền xem bản dịch này"
            );
        }

        return TranslationDetailResponse.builder()
                .id(session.getId())
                .translationType(session.getTranslationType())
                .status(session.getStatus())
                .sourceUrl(session.getSourceUrl())
                .resultUrl(session.getResultUrl())
                .inputContent(session.getInputContent())
                .outputContent(session.getOutputContent())
                .processingTimeMs(session.getProcessingTimeMs())
                .aiVersion(session.getAiVersion())
                .errorMessage(session.getErrorMessage())
                .createdAt(session.getCreatedAt())
                .build();
    }

    @Override
    public PaginationResponseDTO<List<TranslationHistoryResponse>> getAllHistoryForAdmin(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<TranslationSession> sessionPage =
                repository.findAllByOrderByCreatedAtDesc(pageable);

        List<TranslationHistoryResponse> data =
                sessionPage.getContent()
                        .stream()
                        .map(session ->
                                TranslationHistoryResponse.builder()
                                        .id(session.getId())
                                        .translationType(session.getTranslationType())
                                        .status(session.getStatus())
                                        .inputContent(session.getInputContent())
                                        .outputContent(session.getOutputContent())
                                        .sourceUrl(session.getSourceUrl())
                                        .resultUrl(session.getResultUrl())
                                        .createdAt(session.getCreatedAt())
                                        .userId(session.getUser().getId())
                                        .build()
                        )
                        .toList();

        return PaginationResponseDTO
                .<List<TranslationHistoryResponse>>builder()
                .totalItems(sessionPage.getTotalElements())
                .totalPages(sessionPage.getTotalPages())
                .currentPage(sessionPage.getNumber())
                .pageSize(sessionPage.getSize())
                .data(data)
                .build();
    }
}
