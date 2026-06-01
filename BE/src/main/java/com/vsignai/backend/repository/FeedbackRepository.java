package com.vsignai.backend.repository;

import com.vsignai.backend.entity.Feedback;
import com.vsignai.backend.enums.feedback.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository
        extends JpaRepository<Feedback, Long> {

    Page<Feedback>
    findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    @EntityGraph(
            attributePaths = {
                    "user",
                    "translationSession"
            }
    )
    Page<Feedback>
    findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );


}