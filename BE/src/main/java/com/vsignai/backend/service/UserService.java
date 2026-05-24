package com.vsignai.backend.service;

import com.vsignai.backend.entity.User;

public interface UserService {

    User findByEmail(String email);
}