package com.vsignai.backend.service.ipml;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.service.AuthService;
import com.vsignai.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        public String register(String email, String password, String name) {

            if(userRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("Email already exists");
            }

            User user = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .role(User.Role.USER)
                    .status(User.UserStatus.ACTIVE)
                    .build();

            userRepository.save(user);

            return jwtService.generateToken(email);
        }

        public String login(String email, String password) {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if(!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Wrong password");
            }

            return jwtService.generateToken(email);
        }
}

