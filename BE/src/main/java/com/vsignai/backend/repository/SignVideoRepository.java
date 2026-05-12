package com.vsignai.backend.repository;

import com.vsignai.backend.entity.SignVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignVideoRepository
        extends JpaRepository<SignVideo, Long> {

    Optional<SignVideo> findByKeyword(String keyword);
}