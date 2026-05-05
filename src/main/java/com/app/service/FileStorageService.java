package com.app.service;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Tika tika = new Tika();

    // 📁 dossier configurable
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    private static final List<String> ALLOWED_EXT = List.of(
            ".jpg", ".jpeg", ".png", ".pdf"
    );

    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2MB

    // =========================
    // 📁 Sauvegarde fichier
    // =========================
    public String saveFile(MultipartFile file) throws IOException {

        validateFile(file);

        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);

        // 🔒 nom sécurisé
        String newFileName = UUID.randomUUID() + extension;

        Path uploadPath = Paths.get(uploadDir, "cheques");
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(newFileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return newFileName;
    }

    // =========================
    // 🔒 Validation
    // =========================
    private void validateFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Fichier vide");
        }

        // 🔒 taille
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("Fichier trop volumineux (max 2MB)");
        }

        // 🔒 extension
        String filename = file.getOriginalFilename();
        if (filename == null || ALLOWED_EXT.stream().noneMatch(filename.toLowerCase()::endsWith)) {
            throw new RuntimeException("Extension non autorisée");
        }

        // 🔒 type réel (anti fake file)
        String detectedType = tika.detect(file.getInputStream());

        if (!ALLOWED_TYPES.contains(detectedType)) {
            throw new RuntimeException("Type de fichier non autorisé");
        }
    }

    // =========================
    // 🔧 utils
    // =========================
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new RuntimeException("Fichier invalide");
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    // =========================
    // 🗑️ suppression
    // =========================
    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, "cheques", fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // 📥 lecture fichier
    // =========================
    public Path loadFile(String fileName) {
        return Paths.get(uploadDir, "cheques").resolve(fileName);
    }
}