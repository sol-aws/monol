package com.example.ordersystem.product.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class S3Service {
    private static final long MAX_IMAGE_SIZE = 15L * 1024 * 1024; // 15MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // 이미지만 허용하고 최대 크기를 검사한 뒤 S3에 업로드한다.
    // DB에는 URL 전체가 아니라 Object Key(products/...)만 저장한다.
    public String upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("상품 이미지를 선택하세요.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("상품 이미지는 15MB 이하만 업로드할 수 있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("JPG, PNG, GIF, WEBP 이미지 파일만 업로드할 수 있습니다.");
        }

        String extension = extensionFromContentType(contentType);
        String objectKey = "products/" + UUID.randomUUID() + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .cacheControl("public, max-age=86400")
                .build();

        try (var inputStream = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
        }

        return objectKey;
    }

    private String extensionFromContentType(String contentType) {
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }

    // 실습용 Public Read Bucket Policy를 사용하므로 브라우저에서 바로 표시할 URL을 만든다.
    public String createImageUrl(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }

        String encodedKey = URLEncoder.encode(objectKey, StandardCharsets.UTF_8)
                .replace("%2F", "/")
                .replace("+", "%20");

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + encodedKey;
    }
}
