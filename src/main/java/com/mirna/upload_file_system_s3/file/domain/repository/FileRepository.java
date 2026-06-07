package com.mirna.upload_file_system_s3.file.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mirna.upload_file_system_s3.file.domain.File;

public interface FileRepository extends JpaRepository<File, Long>{
    
}
