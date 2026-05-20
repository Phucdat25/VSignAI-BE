package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.response.AuthResponse;
import com.vsignai.backend.entity.SubscriptionPlan;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.subscription.PlanCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.enums.user.UserRole;
import com.vsignai.backend.enums.user.UserStatus;
import com.vsignai.backend.exception.AppException;
import com.vsignai.backend.repository.SubscriptionPlanRepository;
import com.vsignai.backend.repository.UserRepository;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.service.AuthService;
import com.vsignai.backend.service.GoogleService;
import com.vsignai.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final GoogleService googleService;
        private final UserSubscriptionRepository userSubscriptionRepository;
        private final SubscriptionPlanRepository subscriptionPlanRepository;

        @Transactional
        @Override
        public AuthResponse register(String email, String password, String name) {

            if(userRepository.findByEmail(email).isPresent()) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
            }

            User user = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .role(UserRole.CUSTOMER)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(user);
            createFreeSubscription(user);

            String token = jwtService.generateToken(user.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .fullName(user.getName())
                    .role(user.getRole())
                    .plan(getUserPlan(user))
                    .build();
        }

        @Override
        public AuthResponse login(String email, String password) {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Người dùng không tồn tạo"));

            if(!passwordEncoder.matches(password, user.getPassword())) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Mật khẩu không đúng");
            }

            String token = jwtService.generateToken(user.getEmail());
            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .fullName(user.getName())
                    .role(user.getRole())
                    .plan(getUserPlan(user))
                    .build();
        }

        @Transactional
        @Override
    public AuthResponse loginWithGoogle(String idToken) {

        var payload = googleService.verifyToken(idToken);

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if(user == null) {

            user = User.builder()
                    .email(email)
                    .name(name)
                    .password("")
                    .role(UserRole.CUSTOMER)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(user);
            createFreeSubscription(user);
        }


        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getName())
                .role(user.getRole())
                .plan(getUserPlan(user))
                .build();
    }

    private PlanCode getUserPlan(User user) {

        UserSubscription subscription =
                userSubscriptionRepository
                        .findByUserAndStatus(
                                user,
                                SubscriptionStatus.ACTIVE
                        )
                        .orElse(null);

        if(subscription == null) {
            return PlanCode.FREE;
        }

        return subscription.getPlan().getCode();
    }

    // FREE PLAN when user register
    private void createFreeSubscription(User user) {

        SubscriptionPlan freePlan =
                subscriptionPlanRepository
                        .findById(1L)
                        .orElseThrow(() ->
                                new AppException(HttpStatus.BAD_REQUEST, "Không tìm thấy gói miễn phí"));

        UserSubscription subscription =
                UserSubscription.builder()
                        .user(user)
                        .plan(freePlan)
                        .status(SubscriptionStatus.ACTIVE)
                        .isAutoRenew(false)
                        .cancelAtPeriodEnd(false)
                        .startedAt(LocalDateTime.now())
                        .currentPeriodStart(LocalDateTime.now())
                        .currentPeriodEnd(null)
                        .build();

        userSubscriptionRepository.save(subscription);
    }
}

