package com.mirna.upload_file_system_s3.storage.infra;

import java.io.InputStream;

public interface StorageStrategy {
    void uploadFile(String bucketName, String objectId, InputStream inputStream) throws Exception;
    byte[] downloadFile(String bucketName, String objectId) throws Exception;
}
