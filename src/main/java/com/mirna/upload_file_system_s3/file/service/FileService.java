package com.mirna.upload_file_system_s3.file.service;

import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mirna.upload_file_system_s3.file.domain.File;
import com.mirna.upload_file_system_s3.file.domain.repository.FileRepository;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Service
public class FileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private FileRepository repository;

    @Value("${s3.bucket.name}")
    private String bucketName;

    @Value("${s3.env}")
    private String env;

    public String uploadFileImage(MultipartFile file) throws Exception {
        
        var inputStream = file.getInputStream();
        var objectId = UUID.randomUUID().toString();

        this.putObject(objectId, inputStream);

        var fileEntity = new File(objectId);
        repository.saveAndFlush(fileEntity);

        return objectId;
    }

    public byte[] getFileImage(String objectId) throws Exception {
        return getObject(objectId);
    }

    public void putObject(String objectId, InputStream inputStream) throws Exception {

        if (env.contentEquals("LOCAL")) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName).object(objectId)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType("image/png").build());
        }
    }

    public byte[] getObject(String objectId) throws Exception {

        InputStream stream = null;

        if (env.contentEquals("LOCAL")) {
            stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName).object(objectId).build());
        }

        return IOUtils.toByteArray(stream);
    }
}
