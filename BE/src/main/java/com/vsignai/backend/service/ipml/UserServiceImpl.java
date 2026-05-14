package com.vsignai.backend.service.ipml;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.exception.AppException;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(HttpStatus.BAD_REQUEST,"User không tồn tại"));
    }
}
