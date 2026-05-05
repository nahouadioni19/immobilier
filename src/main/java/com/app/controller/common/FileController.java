package com.app.controller.common;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.app.security.UserPrincipal;
import com.app.service.FileStorageService;
import com.app.service.recouvre.EncaisseService;

import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;
    private final EncaisseService encaisseService;

    public FileController(FileStorageService fileStorageService,
                          EncaisseService encaisseService) {
        this.fileStorageService = fileStorageService;
        this.encaisseService = encaisseService;
    }

    // =========================================================
    // 🔒 CHEQUE SECURISÉ (SAAS)
    // =========================================================
    @GetMapping("/cheque/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getCheque(
            @PathVariable String filename,
            @AuthenticationPrincipal UserPrincipal principal) throws Exception {

        if (principal == null || principal.getUtilisateur() == null) {
            throw new SecurityException("Non autorisé");
        }

        // 🔒 anti path traversal
        filename = FilenameUtils.getName(filename);

        Integer agenceId = principal.getUtilisateur().getAgence().getId();

        // 🔒 vérification appartenance agence
        encaisseService.findByChequeAndAgence(filename, agenceId)
                .orElseThrow(() -> new SecurityException("Accès refusé"));

        Path file = fileStorageService.loadFile(filename);

        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toUri());

        String contentType = Files.probeContentType(file);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(Files.size(file))
                .body(resource);
    }
}

/*package com.app.controller.common;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.app.security.UserPrincipal;
import com.app.service.FileStorageService;
import com.app.service.recouvre.EncaisseService;
import com.app.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;
    private final EncaisseService encaisseService;

    public FileController(FileStorageService fileStorageService,
                          EncaisseService encaisseService) {
        this.fileStorageService = fileStorageService;
        this.encaisseService = encaisseService;
    }

    // =========================================================
    // ✅ NOUVEAU : accès sécurisé chèque (SaaS)
    // =========================================================
    @GetMapping("/cheque/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getCheque(
            @PathVariable String filename,
            @AuthenticationPrincipal UserPrincipal principal) throws IOException {

        // 🔒 anti hack
        filename = filename.replace("..", "");

        Integer agenceId = principal.getUtilisateur().getAgence().getId();

        // 🔒 vérifier appartenance agence
        encaisseService.findByChequeAndAgence(filename, agenceId)
                .orElseThrow(() -> new SecurityException("Accès refusé"));

        Path file = fileStorageService.loadFile(filename);

        Resource resource = new UrlResource(file.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    // =========================================================
    // ⚠️ ANCIEN : accès par hash (à garder temporairement)
    // =========================================================
    @GetMapping({"/dossier/{hash}", "/{hash}"})
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String hash) throws IOException {

        try (Stream<Path> stream = Files.walk(Paths.get(Constants.BASE_DIRECTORY))) {

            Optional<Path> pathSearch = stream
                    .filter(file -> !Files.isDirectory(file)
                            && FilenameUtils.getName(file.toFile().getName()).equals(hash))
                    .findFirst();

            if (pathSearch.isPresent()) {
                Path path = pathSearch.get();
                File file = path.toFile();

                String mimeTypeStr = Files.probeContentType(path);
                MediaType mimeType = MediaType.parseMediaType(mimeTypeStr);

                HttpHeaders headers = new HttpHeaders();
                headers.add("content-disposition", "inline;filename=" + file.getName());

                InputStreamResource resource =
                        new InputStreamResource(new FileInputStream(file));

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(file.length())
                        .contentType(mimeType)
                        .body(resource);
            }
        }

        return ResponseEntity.noContent().build();
    }
}*/