package com.mirna.upload_file_system_s3.file.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mirna.upload_file_system_s3.file.service.FileService;

@RestController
@RequestMapping("images")
public class FileController {

    @Autowired
    private FileService uploadService;

    @PostMapping
    public String uploadFileImage(@RequestParam MultipartFile file) throws Exception {
        return uploadService.uploadFileImage(file);
    }

    @GetMapping(value = "/{objectId}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getFileImage(@PathVariable String objectId) throws Exception {
        return uploadService.getFileImage(objectId);
    }
}
