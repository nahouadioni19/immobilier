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
public class FileMovedPojo implements Serializable {

    private static final long serialVersionUID = 1L;

    private File file;
    private String oldPath;
    private String newPath;

}
