package com.mirna.upload_file_system_s3.config.local;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class LocalClientConfig {

    @Value("${s3.local.endpoint}")
    private String endpoint;

    @Value("${s3.local.credentials.access.key}")
    private String accessKey;

    @Value("${s3.local.credentials.secret.key}")
    private String secretKey;

    @Bean
    MinioClient minioClient() {
        return MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build();
    }

}