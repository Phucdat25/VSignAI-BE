package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.response.SignVideoResponse;
import com.vsignai.backend.entity.SignVideo;
import com.vsignai.backend.entity.User;
import com.vsignai.backend.entity.UserSubscription;
import com.vsignai.backend.enums.feature.FeatureCode;
import com.vsignai.backend.enums.subscription.SubscriptionStatus;
import com.vsignai.backend.exception.AppException;
import com.vsignai.backend.repository.SignVideoRepository;
import com.vsignai.backend.repository.UserSubscriptionRepository;
import com.vsignai.backend.service.SignVideoService;
import com.vsignai.backend.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignVideoServiceImpl implements SignVideoService {

    private final CloudinaryServiceImpl cloudinaryService;
    private final SignVideoRepository repository;
    private final UsageService usageService;
    private final UserSubscriptionRepository subscriptionRepository;

    @Override
    public SignVideo upload(String keyword, MultipartFile file) {

        keyword = keyword.trim().toLowerCase();

        Map result = cloudinaryService.uploadVideo(file);

        String publicId =
                result.get("public_id").toString();

        String videoUrl =
                result.get("secure_url").toString();

        SignVideo signVideo = new SignVideo();

        signVideo.setKeyword(keyword);
        signVideo.setPublicId(publicId);
        signVideo.setVideoUrl(videoUrl);

        return repository.save(signVideo);
    }

    @Override
    public SignVideoResponse getByKeyword(String keyword, User user) {

        keyword = keyword
                .trim()
                .toLowerCase()
                .replaceAll("[.!?;,]+$", "");

        SignVideo video = repository
                .findByKeywordIgnoreCase(keyword)
                .orElseThrow(() ->
                        new AppException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy video phù hợp"
                        ));

        UserSubscription subscription =
                subscriptionRepository
                        .findByUserAndStatus(
                                user,
                                SubscriptionStatus.ACTIVE
                        )
                        .orElse(null);

        usageService.checkAndSaveUsage(
                user,
                subscription,
                FeatureCode.TRANSLATION,
                video.getDurationSeconds()
        );
        String thumbnailUrl =
                "https://res.cloudinary.com/dinw9zchn/video/upload/so_1/"
                        + video.getPublicId()
                        + ".jpg";

        return new SignVideoResponse(
                video.getKeyword(),
                video.getVideoUrl(),
                thumbnailUrl
        );
    }
}