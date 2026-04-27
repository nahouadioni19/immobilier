package com.app.service.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.transaction.Transactional;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import com.app.pojo.FichierPOJO;
import lombok.extern.slf4j.Slf4j;

@Component
@Transactional
@Slf4j
public class StorageService {

    public boolean store(FichierPOJO fichier, MultipartFile file) throws IOException {
        boolean isStored = true;
        File directory = new File(fichier.getDossier());
        File tempFile = new File(FileUtils.getTempDirectory(), fichier.getNom());
        file.transferTo(tempFile);
        try {
            FileUtils.moveToDirectory(tempFile, directory, true);
        } catch (FileExistsException e) {
            isStored = false;
        }
        FileUtils.deleteQuietly(tempFile);

        return isStored;
    }

    public void delete(String filename) {
        this.delete(this.getPath(filename));
    }

    public void delete(Path path) {
        //
    }

    public Path getPath(String filename) {
        return Paths.get(filename);
    }

    public String getFileChecksum(MultipartFile file) throws IOException {
        return DigestUtils.md5DigestAsHex(file.getBytes());
    }

    public File getTempFileFromFichier(MultipartFile file, String dossier) throws IOException {
        FichierPOJO fichier = getFichier(file, dossier);
        File tempFile = new File(FileUtils.getTempDirectory(), fichier.getNom());
        try {
            file.transferTo(tempFile);
        } catch (Exception e) {
            // si file already in tmp folder in linux, error occur but not windows sys
            log.info(e.getMessage(), e);
        }
        return tempFile;
    }

    public FichierPOJO getFichier(MultipartFile file, String dossier) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        final String hash = this.getFileChecksum(file);
        final String filename = hash + "." + extension;
        return FichierPOJO.builder().nom(filename).nomOrigine(file.getOriginalFilename()).lien(dossier + filename)
                .dossier(dossier).build();
    }
}

