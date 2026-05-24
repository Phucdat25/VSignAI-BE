package com.vsignai.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vsignai.backend.exceptions.AppException;
import com.vsignai.backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public Map uploadVideo(MultipartFile file) {

        try {

            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", "sign-language"
                    )
            );

        } catch (IOException e) {
            throw new AppException(
                    "UPLOAD_FAILED",
                    "Upload failed",
                    HttpStatus.BAD_REQUEST
            );
        }
    }



}