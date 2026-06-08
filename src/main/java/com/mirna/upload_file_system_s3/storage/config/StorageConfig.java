package com.mirna.upload_file_system_s3.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.mirna.upload_file_system_s3.storage.infra.MinioStrategy;
import com.mirna.upload_file_system_s3.storage.infra.S3Strategy;
import com.mirna.upload_file_system_s3.storage.infra.StorageStrategy;

import io.minio.MinioClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class StorageConfig {
    
    @Value("${s3.local.endpoint}")
    private String endpoint;

    @Value("${s3.local.credentials.access.key}")
    private String accessKey;

    @Value("${s3.local.credentials.secret.key}")
    private String secretKey;

    @Bean
    @Profile("local")
    public StorageStrategy minioStrategy() {
        return new MinioStrategy(MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build());
    }

    @Bean
    @Profile("prod")
    public StorageStrategy awsS3Strategy() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return new S3Strategy(S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build());
    }
    
}
