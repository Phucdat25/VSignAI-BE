package com.vsignai.backend.service.ipml;

import com.vsignai.backend.dto.response.SignVideoResponse;
import com.vsignai.backend.entity.SignVideo;
import com.vsignai.backend.repository.SignVideoRepository;
import com.vsignai.backend.service.SignVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignVideoServiceImpl implements SignVideoService {

    private final CloudinaryServiceImpl cloudinaryService;
    private final SignVideoRepository repository;


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
    public SignVideoResponse getByKeyword(String keyword) {

        keyword = keyword
                .trim()
                .replaceAll("[.!?;,]+$", "");

        SignVideo video = repository
                .findByKeywordIgnoreCase(keyword)
                .orElseThrow();

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