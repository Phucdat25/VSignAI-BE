package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.CreateTranslationSessionRequest;
import com.vsignai.backend.dto.response.AiPredictResponse;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.feature.FeatureCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.enums.translation.TranslationStatus;
import com.vsignai.backend.enums.translation.TranslationType;
import com.vsignai.backend.exception.AppException;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.service.AiService;
import com.vsignai.backend.service.TranslationSessionService;
import com.vsignai.backend.service.UsageService;
import com.vsignai.backend.util.MultipartInputStreamFileResource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final TranslationSessionService translationSessionService;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private final UsageService usageService;
    private final UserSubscriptionRepository subscriptionRepository;


    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public AiPredictResponse predict(MultipartFile file, Integer durationSeconds) {

        long start = System.currentTimeMillis();

        if (file == null || file.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Video không được để trống");
        }

        if (durationSeconds == null || durationSeconds <= 0) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Thời lượng sử dụng không hợp lệ");
        }

        User user = getCurrentUser();

        UserSubscription subscription = subscriptionRepository
                .findTopByUserAndStatusOrderByCurrentPeriodEndDesc(user, SubscriptionStatus.ACTIVE)
                .orElse(null);

        AiPredictResponse response = callAiService(file, user);

        if (response == null) {
            throw new AppException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "AI service không trả kết quả"
            );
        }

        if (!"success".equalsIgnoreCase(response.getStatus())) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    response.getMessage() != null
                            ? response.getMessage()
                            : "AI không nhận diện được ký hiệu"
            );
        }

        long processingTime = System.currentTimeMillis() - start;

        usageService.checkAndSaveUsage(
                user,
                subscription,
                FeatureCode.SIGN_RECOGNITION,
                durationSeconds
        );

        translationSessionService.createSession(
                CreateTranslationSessionRequest
                        .builder()
                        .user(user)
                        .translationType(
                                TranslationType.SIGN_TO_SPEECH
                        )
                        .status(
                                TranslationStatus.SUCCESS
                        )
                        .inputContent(
                                file.getOriginalFilename()
                        )
                        .outputContent(
                                response.getPredictedGloss()
                        )
                        .processingTimeMs(
                                processingTime
                        )
                        .aiVersion(
                                "sign-recognition-v1"
                        )
                        .build()
        );

        return response;
    }

    private AiPredictResponse callAiService(MultipartFile file,User user) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("file", new MultipartInputStreamFileResource(
                    file.getInputStream(),
                    file.getOriginalFilename()
            ));

//            body.add("stride", "15");
//            body.add("confidence_threshold", "70.0");

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<AiPredictResponse> response =
                    restTemplate.postForEntity(
                            aiServiceUrl,
                            requestEntity,
                            AiPredictResponse.class
                    );

            return response.getBody();

        } catch (Exception e) {

            translationSessionService.createSession(
                    CreateTranslationSessionRequest
                            .builder()
                            .user(user)
                            .translationType(
                                    TranslationType.SIGN_TO_SPEECH
                            )
                            .status(
                                    TranslationStatus.FAILED
                            )
                            .errorMessage(
                                    e.getMessage()
                            )
                            .aiVersion(
                                    "sign-recognition-v1"
                            )
                            .build()
            );

            throw new AppException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Gọi AI service thất bại: " + e.getMessage()
            );
        }
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        throw new AppException(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập");
    }
}
