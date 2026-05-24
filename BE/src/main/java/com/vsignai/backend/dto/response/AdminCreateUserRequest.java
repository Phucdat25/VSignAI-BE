package com.vsignai.backend.dto.response;

import com.vsignai.backend.enums.user.UserRole;
import com.vsignai.backend.enums.user.UserStatus;
import lombok.Data;

@Data
public class AdminCreateUserRequest {

    private String name;
    private String email;
    private String password;
    private UserRole role;
    private UserStatus status;
}