package com.vsignai.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sign_videos")
@Getter
@Setter
public class SignVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyword;

    @Column(nullable = false)
    private String publicId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String videoUrl;

    @Column(nullable = false)
    private Integer durationSeconds;

}
