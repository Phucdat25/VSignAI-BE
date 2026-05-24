package com.vsignai.backend.repository;

import com.vsignai.backend.entity.SignVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SignVideoRepository
        extends JpaRepository<SignVideo, Long> {

    Optional<SignVideo> findByKeyword(String keyword);
    @Query("""
    SELECT s
    FROM SignVideo s 
    WHERE LOWER(s.keyword) = LOWER(:keyword)
 """)
    Optional<SignVideo> findByKeywordIgnoreCase(
            @Param("keyword") String keyword
    );
}