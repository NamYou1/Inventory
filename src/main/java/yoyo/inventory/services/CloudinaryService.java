package yoyo.inventory.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    /**
     * Uploads a file to a specific folder in Cloudinary and returns the secure URL.
     *
     * @param file       the multipart file to upload
     * @param folderName the target folder name in Cloudinary
     * @return the secure URL of the uploaded file
     */
    String uploadFile(MultipartFile file, String folderName);

    /**
     * Uploads a file to the default folder in Cloudinary and returns the secure URL.
     *
     * @param file the multipart file to upload
     * @return the secure URL of the uploaded file
     */
    String uploadFile(MultipartFile file);
}
