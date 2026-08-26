package com.kampuskart.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${app.upload.dir:../uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body(Map.of("error", "File too large (max 10MB)"));
            }
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only JPG, PNG, GIF, WebP images allowed"));
            }

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String ext = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }
            String filename = UUID.randomUUID() + ext;
            Path filepath = uploadPath.resolve(filename);
            file.transferTo(filepath.toFile());

            return ResponseEntity.ok(Map.of(
                "url", "/uploads/" + filename,
                "filename", filename
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }
}
