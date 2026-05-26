package yoyo.inventory.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yoyo.inventory.execption.CloudinaryUploadException;
import yoyo.inventory.services.CloudinaryService;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            throw new CloudinaryUploadException(HttpStatus.BAD_REQUEST, "File cannot be empty");
        }

        try {
            log.info("Uploading file to Cloudinary in folder: {}", folderName);
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", folderName)
            );
            
            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("File uploaded successfully. Secure URL: {}", secureUrl);
            return secureUrl;
        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw new CloudinaryUploadException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file to Cloudinary: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        // Default folder for general inventory uploads
        return uploadFile(file, "inventory");
    }
}
