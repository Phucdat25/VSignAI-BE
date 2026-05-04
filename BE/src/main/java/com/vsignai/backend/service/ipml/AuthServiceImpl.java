package com.vsignai.backend.service.ipml;

import com.vsignai.backend.entity.User;
import com.vsignai.backend.enums.user.UserRole;
import com.vsignai.backend.enums.user.UserStatus;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.service.AuthService;
import com.vsignai.backend.service.GoogleService;
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
        private final GoogleService googleService;

        public String register(String email, String password, String name) {

            if(userRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("Email already exists");
            }

            User user = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .role(UserRole.CUSTOMER)
                    .status(UserStatus.ACTIVE)
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

    public String loginWithGoogle(String idToken) {

        var payload = googleService.verifyToken(idToken);

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .password("") // Google user không cần password
                            .role(UserRole.CUSTOMER)
                            .status(UserStatus.ACTIVE)
                            .build();
                    return userRepository.save(newUser);
                });

        return jwtService.generateToken(user.getEmail());
    }
}

