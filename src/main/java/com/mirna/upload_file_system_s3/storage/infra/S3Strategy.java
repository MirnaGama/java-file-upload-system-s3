package com.mirna.upload_file_system_s3.storage.infra;
import java.io.InputStream;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import org.apache.commons.io.IOUtils;

public class S3Strategy implements StorageStrategy {
    private final S3Client s3Client;

    public S3Strategy(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void uploadFile(String bucketName, String objectId, InputStream inputStream) throws Exception {
        s3Client.putObject(
            PutObjectRequest.builder().bucket(bucketName).key(objectId)
            .contentType("image/png").build(),
            RequestBody.fromInputStream(inputStream, inputStream.available())
        );
    }

    @Override
    public byte[] downloadFile(String bucketName, String objectId) throws Exception {
        var stream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectId)
                    .build()); 
        return IOUtils.toByteArray(stream);
    }
}