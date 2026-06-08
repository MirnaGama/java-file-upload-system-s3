package com.mirna.upload_file_system_s3.file.service;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mirna.upload_file_system_s3.file.domain.File;
import com.mirna.upload_file_system_s3.file.domain.repository.FileRepository;
import com.mirna.upload_file_system_s3.storage.infra.StorageStrategy;

@Service
public class FileService {

    @Autowired
    private StorageStrategy storageStrategy;

    @Autowired
    private FileRepository repository;

    @Value("${s3.bucket.name}")
    private String bucketName;

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

    private void putObject(String objectId, InputStream inputStream) throws Exception {
      storageStrategy.uploadFile(bucketName, objectId, inputStream);
    }

    private byte[] getObject(String objectId) throws Exception {
        return storageStrategy.downloadFile(bucketName, objectId);
    }
}
