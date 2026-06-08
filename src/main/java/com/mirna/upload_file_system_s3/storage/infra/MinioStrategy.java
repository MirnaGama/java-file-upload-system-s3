package com.mirna.upload_file_system_s3.storage.infra;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

public class MinioStrategy implements StorageStrategy {

    private final MinioClient minioClient;

    public MinioStrategy(MinioClient minioClient) {
        this.minioClient = minioClient;
    }
 
    @Override
    public void uploadFile(String bucketName, String objectId, InputStream inputStream) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName).object(objectId)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType("image/png").build());
    }

    @Override
    public byte[] downloadFile(String bucketName, String objectId) throws Exception {
        var stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName).object(objectId).build());
        return IOUtils.toByteArray(stream);
    }
    
}
