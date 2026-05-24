package com.vsignai.backend.service.impl;

import com.vsignai.backend.config.RequestContext;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.enums.user.UserRole;
import com.vsignai.backend.enums.user.UserStatus;
import com.vsignai.backend.exceptions.AppException;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.service.AuthService;
import com.vsignai.backend.service.GoogleService;
import com.vsignai.backend.service.JwtService;
import com.vsignai.backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final GoogleService googleService;
        private final SubscriptionService subscriptionService;

        @Override
        @Transactional
        public String register(String email, String password, String name) {

            if (userRepository.findByEmail(email).isPresent()) {
                throw new AppException(
                        "EMAIL_ALREADY_EXISTS",
                        "Email already exists",
                        HttpStatus.CONFLICT
                );
            }

            User user = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .role(UserRole.CUSTOMER)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(user);

            subscriptionService.provisionFreeSubscriptionIfAbsent(user.getId());

            log.info(
                    "User registered | requestId={} | userId={} | email={}",
                    RequestContext.getRequestId(),
                    user.getId(),
                    email
            );

            return jwtService.generateToken(email);
        }

        @Override
        public String login(String email, String password) {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new AppException(
                                    "USER_NOT_FOUND",
                                    "User not found",
                                    HttpStatus.NOT_FOUND
                            )
                    );

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new AppException(
                        "INVALID_CREDENTIALS",
                        "Wrong password",
                        HttpStatus.UNAUTHORIZED
                );
            }

            return jwtService.generateToken(email);
        }

        @Override
        @Transactional
        public String loginWithGoogle(String idToken) {

            var payload = googleService.verifyToken(idToken);

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            boolean isNewUser =
                    userRepository.findByEmail(email).isEmpty();

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .email(email)
                                .name(name)
                                .password("")
                                .role(UserRole.CUSTOMER)
                                .status(UserStatus.ACTIVE)
                                .build();
                        return userRepository.save(newUser);
                    });

            subscriptionService.provisionFreeSubscriptionIfAbsent(user.getId());

            log.info(
                    "Google login successful | requestId={} | userId={} | email={} | newUser={}",
                    RequestContext.getRequestId(),
                    user.getId(),
                    email,
                    isNewUser
            );

            return jwtService.generateToken(user.getEmail());
        }
}
