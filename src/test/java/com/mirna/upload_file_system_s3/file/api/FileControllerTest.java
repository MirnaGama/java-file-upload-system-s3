package com.mirna.upload_file_system_s3.file.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.mirna.upload_file_system_s3.file.service.FileService;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private FileController fileController;

    @Mock
    private FileService fileService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    @DisplayName("Should upload file and return the generated objectId")
    void shouldUploadFileImageSuccessfully() throws Exception {
        String expectedObjectId = "mock-uuid-1234";
        byte[] fileContent = "dummy image content".getBytes();
        
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", 
                "test-image.png", 
                MediaType.IMAGE_PNG_VALUE, 
                fileContent
        );

        when(fileService.uploadFileImage(any(MultipartFile.class))).thenReturn(expectedObjectId);

        mockMvc.perform(multipart("/files").file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedObjectId));
    }

    @Test
    @DisplayName("Should return byte array and correct media type when fetching file")
    void shouldGetFileImageSuccessfully() throws Exception {
        String objectId = "mock-uuid-1234";
        byte[] expectedBytes = "mock file bytes".getBytes();

        when(fileService.getFileImage(objectId)).thenReturn(expectedBytes);

        mockMvc.perform(get("/files/{objectId}", objectId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(expectedBytes));
    }
}