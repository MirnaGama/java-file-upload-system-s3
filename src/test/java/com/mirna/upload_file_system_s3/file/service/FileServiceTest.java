package com.mirna.upload_file_system_s3.file.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mirna.upload_file_system_s3.file.domain.File;
import com.mirna.upload_file_system_s3.file.domain.repository.FileRepository;
import com.mirna.upload_file_system_s3.storage.infra.StorageStrategy;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private StorageStrategy storageStrategy;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private MultipartFile multipartFile;

    @Captor
    private ArgumentCaptor<File> fileEntityCaptor;

    private final String BUCKET_NAME = "test-bucket";

    @BeforeEach
    void setUp() {
        // @Value property
        ReflectionTestUtils.setField(fileService, "bucketName", BUCKET_NAME);
    }

    @Test
    @DisplayName("Should upload file using strategy, save entity, and return generated ID")
    void shouldUploadFileImageSuccessfully() throws Exception {
        String mockContent = "mock image content";
        InputStream mockStream = new ByteArrayInputStream(mockContent.getBytes());
        
        when(multipartFile.getInputStream()).thenReturn(mockStream);
        
        when(fileRepository.saveAndFlush(any(File.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String objectId = fileService.uploadFileImage(multipartFile);

        assertNotNull(objectId);

        verify(storageStrategy).uploadFile(eq(BUCKET_NAME), eq(objectId), eq(mockStream));

        verify(fileRepository).saveAndFlush(fileEntityCaptor.capture());
        File capturedFile = fileEntityCaptor.getValue();
        
        assertNotNull(capturedFile);
        assertEquals(objectId, capturedFile.getObjectId());
    }

    @Test
    @DisplayName("Should retrieve file bytes from the storage strategy")
    void shouldGetFileImageSuccessfully() throws Exception {
        String objectId = "test-uuid-1234";
        byte[] expectedBytes = "mock file bytes".getBytes();
        
        when(storageStrategy.downloadFile(BUCKET_NAME, objectId)).thenReturn(expectedBytes);

        byte[] resultBytes = fileService.getFileImage(objectId);
      
        assertArrayEquals(expectedBytes, resultBytes);

        verify(storageStrategy).downloadFile(BUCKET_NAME, objectId);
    }
}