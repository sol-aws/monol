package com.example.ordersystem.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    // AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY는 Kubernetes Secret에서 환경변수로 주입한다.
    // AWS SDK의 기본 Credential Provider Chain이 환경변수를 자동으로 사용한다.
    @Bean
    public S3Client s3Client(@Value("${aws.region}") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
}
