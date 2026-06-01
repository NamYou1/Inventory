package yoyo.inventory.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Uploads a file to a specific folder in the S3-compatible storage and returns the URL.
     *
     * @param file       the multipart file to upload
     * @param folderName the target folder name
     * @return the public URL of the uploaded file
     */
    String uploadFile(MultipartFile file, String folderName);

    /**
     * Uploads a file to the default folder in the S3-compatible storage and returns the URL.
     *
     * @param file the multipart file to upload
     * @return the public URL of the uploaded file
     */
    String uploadFile(MultipartFile file);
}
