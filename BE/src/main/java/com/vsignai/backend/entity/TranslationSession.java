package com.vsignai.backend.entity;

import com.vsignai.backend.enums.translation.TranslationStatus;
import com.vsignai.backend.enums.translation.TranslationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "translation_sessions",
        indexes = {

                @Index(
                        name = "idx_translation_user",
                        columnList = "user_id"
                ),

                @Index(
                        name = "idx_translation_type",
                        columnList = "translation_type"
                ),

                @Index(
                        name = "idx_translation_status",
                        columnList = "status"
                ),

                @Index(
                        name = "idx_translation_created",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User thực hiện dịch
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    /**
     * Speech -> Sign
     * Sign -> Speech
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "translation_type",
            nullable = false,
            length = 30
    )
    private TranslationType translationType;

    /**
     * Trạng thái xử lý
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private TranslationStatus status;

    /**
     * URL file đầu vào
     *
     * Speech -> Sign:
     * audio cloudinary url
     *
     * Sign -> Speech:
     * video cloudinary url
     */
    @Column(columnDefinition = "TEXT")
    private String sourceUrl;

    /**
     * URL file kết quả
     *
     * Speech -> Sign:
     * merged sign video
     *
     * Sign -> Speech:
     * có thể null
     */
    @Column(columnDefinition = "TEXT")
    private String resultUrl;

    /**
     * Nội dung đầu vào
     *
     * Speech -> Sign:
     * transcript text
     *
     * Sign -> Speech:
     * raw prediction
     */
    @Column(columnDefinition = "TEXT")
    private String inputContent;

    /**
     * Nội dung đầu ra cuối cùng
     */
    @Column(columnDefinition = "TEXT")
    private String outputContent;

    /**
     * Thời gian xử lý AI (ms)
     */
    private Long processingTimeMs;

    /**
     * Version model AI
     *
     * ví dụ:
     * sign-ai-v1
     * sign-ai-v2
     */
    @Column(length = 100)
    private String aiVersion;

    /**
     * Lý do lỗi nếu FAILED
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Soft delete
     */
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(
            name = "updated_at"
    )
    private LocalDateTime updatedAt;
}