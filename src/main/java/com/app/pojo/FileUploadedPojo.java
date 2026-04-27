package com.app.pojo;

import java.io.File;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadedPojo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String filename;
    private String originalName;

    private File file;
    private String filePath;

    private File dirFile;
    private String dirPath;

}
