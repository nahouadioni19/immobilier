package com.app.controller.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.utils.Constants;



@Controller
@RequestMapping({"/files","/trace"})
public class FileController {

    @GetMapping({"/dossier/{hash}","/{hash}"})
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String hash) throws IOException{
        try (Stream<Path> stream = Files.walk(Paths.get(Constants.BASE_DIRECTORY), Constants.DIRECTORY_FORMAT.split(Constants.PATH_SEPARATOR).length + 1);) {
            Optional<Path> pathSearch = stream.filter(
                                    file -> !Files.isDirectory(file) && FilenameUtils.getName(file.toFile().getName()).equals(hash)
                                  )
                                  .findFirst();

            if (pathSearch.isPresent()){
                Path path = pathSearch.get();
                File file = path.toFile();

                String mimeTypeStr = Files.probeContentType(path);
                MediaType mimeType = MediaType.parseMediaType(mimeTypeStr);

                HttpHeaders headers = new HttpHeaders();
                headers.add("content-disposition", "inline;filename=" + file.getName());
                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

                return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(mimeType)
                    .body(resource);
            }
        }

        return ResponseEntity.noContent().build();
    }

}
