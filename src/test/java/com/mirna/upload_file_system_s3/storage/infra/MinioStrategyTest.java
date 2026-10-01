package com.mirna.upload_file_system_s3.storage.infra;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
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

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@ExtendWith(MockitoExtension.class)
public class MinioStrategyTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioStrategy minioStrategy;

    @Captor
    private ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor;

    @Captor
    private ArgumentCaptor<GetObjectArgs> getObjectArgsCaptor;

    @Test
    @DisplayName("Should upload file and verify PutObjectArgs arguments")
    void shouldUploadFileSuccessfully() throws Exception {
        String bucketName = "test-bucket";
        String objectId = "test-uuid-1234";
        byte[] fileBytes = "dummy image data".getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(fileBytes);

        minioStrategy.uploadFile(bucketName, objectId, inputStream);

        verify(minioClient).putObject(putObjectArgsCaptor.capture());

        PutObjectArgs capturedArgs = putObjectArgsCaptor.getValue();
        assertNotNull(capturedArgs);
        assertEquals(bucketName, capturedArgs.bucket());
        assertEquals(objectId, capturedArgs.object());
        assertEquals("image/png", capturedArgs.contentType());
    }

    @Test
    @DisplayName("Should download file and verify GetObjectArgs arguments")
    void shouldDownloadFileSuccessfully() throws Exception {
        String bucketName = "test-bucket";
        String objectId = "test-uuid-1234";
        byte[] expectedBytes = "dummy image data".getBytes(StandardCharsets.UTF_8);

        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        
        lenient().when(mockResponse.read(any(byte[].class), any(int.class), any(int.class)))
                .thenAnswer(invocation -> {
                    byte[] buffer = invocation.getArgument(0);
                    System.arraycopy(expectedBytes, 0, buffer, 0, expectedBytes.length);
                    return expectedBytes.length; 
                })
                .thenReturn(-1); // Return -1 on the second call to signal end of stream
                
        lenient().when(mockResponse.read(any(byte[].class)))
                .thenAnswer(invocation -> {
                     byte[] buffer = invocation.getArgument(0);
                     System.arraycopy(expectedBytes, 0, buffer, 0, expectedBytes.length);
                     return expectedBytes.length;
                })
                .thenReturn(-1);

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);

        byte[] resultBytes = minioStrategy.downloadFile(bucketName, objectId);

        assertArrayEquals(expectedBytes, resultBytes);

        verify(minioClient).getObject(getObjectArgsCaptor.capture());
        
        GetObjectArgs capturedArgs = getObjectArgsCaptor.getValue();
        assertNotNull(capturedArgs);
        assertEquals(bucketName, capturedArgs.bucket());
        assertEquals(objectId, capturedArgs.object());
    }
}