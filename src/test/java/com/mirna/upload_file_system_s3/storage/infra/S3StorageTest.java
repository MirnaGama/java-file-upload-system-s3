package com.mirna.upload_file_system_s3.storage.infra;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class S3StrategyTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Strategy s3Strategy;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

    @Captor
    private ArgumentCaptor<RequestBody> requestBodyCaptor;

    @Captor
    private ArgumentCaptor<GetObjectRequest> getObjectRequestCaptor;

    @Test
    @DisplayName("Should upload file and verify PutObjectRequest arguments")
    void shouldUploadFileSuccessfully() throws Exception {
        String bucketName = "test-bucket";
        String objectId = "test-uuid-1234";
        byte[] fileBytes = "dummy image data".getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(fileBytes);

        s3Strategy.uploadFile(bucketName, objectId, inputStream);

        verify(s3Client).putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture());

        PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();
        assertNotNull(capturedRequest);
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(objectId, capturedRequest.key());
        assertEquals("image/png", capturedRequest.contentType());

        RequestBody capturedBody = requestBodyCaptor.getValue();
        assertNotNull(capturedBody);
        assertEquals(fileBytes.length, capturedBody.optionalContentLength().orElse(0L));
    }

    @Test
    @DisplayName("Should download file and verify GetObjectRequest arguments")
    void shouldDownloadFileSuccessfully() throws Exception {
        String bucketName = "test-bucket";
        String objectId = "test-uuid-1234";
        byte[] expectedBytes = "dummy image data".getBytes(StandardCharsets.UTF_8);

        InputStream byteArrayInputStream = new ByteArrayInputStream(expectedBytes);
        ResponseInputStream<GetObjectResponse> responseInputStream = new ResponseInputStream<>(
                GetObjectResponse.builder().build(), 
                byteArrayInputStream
        );

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);

        byte[] resultBytes = s3Strategy.downloadFile(bucketName, objectId);

        assertArrayEquals(expectedBytes, resultBytes);

        verify(s3Client).getObject(getObjectRequestCaptor.capture());
        GetObjectRequest capturedRequest = getObjectRequestCaptor.getValue();
        
        assertNotNull(capturedRequest);
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(objectId, capturedRequest.key());
    }
}
