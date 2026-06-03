package com.vsignai.backend.repository;

import com.vsignai.backend.entity.TranslationSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TranslationSessionRepository
        extends JpaRepository<TranslationSession, Long> {

    List<TranslationSession>
    findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<TranslationSession>
    findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    long countByUserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    Page<TranslationSession> findAllByOrderByCreatedAtDesc(Pageable pageable);

}