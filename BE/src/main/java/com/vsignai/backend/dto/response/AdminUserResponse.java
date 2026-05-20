package com.vsignai.backend.dto.response;

import com.vsignai.backend.enums.user.UserRole;
import com.vsignai.backend.enums.user.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;

    private UserRole role;
    private UserStatus status;

    private String planCode;
    private String planName;

    private LocalDateTime joinedAt;
}