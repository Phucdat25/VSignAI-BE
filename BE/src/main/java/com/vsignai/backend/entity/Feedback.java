package com.vsignai.backend.entity;

import com.vsignai.backend.enums.feedback.FeedbackCategory;
import com.vsignai.backend.enums.feedback.FeedbackStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "feedbacks",
        indexes = {
                @Index(
                        name = "idx_feedback_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_feedback_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_feedback_session",
                        columnList = "translation_session_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "translation_session_id",
            nullable = false
    )
    private TranslationSession translationSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackCategory category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedbackContent;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status = FeedbackStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}