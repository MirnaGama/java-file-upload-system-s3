package com.mirna.upload_file_system_s3.file.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Table(name = "files")
@Entity
@Getter
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "objectId cannot be null")
    private String objectId;

    public File(String objectId) {
        this.objectId = objectId;
    }
}
