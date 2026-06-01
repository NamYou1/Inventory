package yoyo.inventory.services.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import yoyo.inventory.execption.FileUploadException;
import yoyo.inventory.services.FileStorageService;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RustfsStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;

    @Value("${rustfs.bucket}")
    private String bucketName;

    @Value("${rustfs.endpoint}")
    private String endpoint;

    @PostConstruct
    public void initBucket() {
        try {
            log.info("Checking if RustFS bucket '{}' exists...", bucketName);
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("Bucket '{}' already exists.", bucketName);
            setBucketPublicPolicy();
        } catch (NoSuchBucketException e) {
            log.info("Bucket '{}' does not exist. Creating bucket...", bucketName);
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            log.info("Bucket '{}' created successfully.", bucketName);
            setBucketPublicPolicy();
        } catch (Exception e) {
            log.warn("Could not check, create or configure bucket on startup. It might be created later or the server is down: {}", e.getMessage());
        }
    }

    private void setBucketPublicPolicy() {
        try {
            log.info("Setting public read policy on bucket '{}'...", bucketName);
            String publicPolicy = "{\n" +
                    "  \"Version\": \"2012-10-17\",\n" +
                    "  \"Statement\": [\n" +
                    "    {\n" +
                    "      \"Sid\": \"PublicRead\",\n" +
                    "      \"Effect\": \"Allow\",\n" +
                    "      \"Principal\": \"*\",\n" +
                    "      \"Action\": [\"s3:GetObject\"],\n" +
                    "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            
            s3Client.putBucketPolicy(PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(publicPolicy)
                    .build());
            log.info("Public read policy set successfully on bucket '{}'.", bucketName);
        } catch (Exception e) {
            log.error("Failed to set public read policy on bucket '{}': {}", bucketName, e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException(HttpStatus.BAD_REQUEST, "File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unnamed_file";
        }
        
        // Sanitize folder name
        String folder = (folderName == null || folderName.trim().isEmpty()) ? "general" : folderName.trim();
        // Generate unique filename
        String uniqueKey = folder + "/" + UUID.randomUUID() + "_" + originalFilename.replaceAll("\\s+", "_");

        try {
            log.info("Uploading file to RustFS bucket: {}, key: {}", bucketName, uniqueKey);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

            // Construct public URL
            String baseUrl = endpoint.endsWith("/") ? endpoint : endpoint + "/";
            String fileUrl = baseUrl + bucketName + "/" + uniqueKey;

            log.info("File uploaded successfully. URL: {}", fileUrl);
            return fileUrl;
        } catch (IOException e) {
            log.error("Failed to read bytes from multipart file", e);
            throw new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read file: " + e.getMessage(), e);
        } catch (S3Exception e) {
            log.error("Error occurred while uploading to RustFS S3", e);
            throw new FileUploadException(HttpStatus.INTERNAL_SERVER_ERROR, "RustFS upload error: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, "inventory");
    }
}
