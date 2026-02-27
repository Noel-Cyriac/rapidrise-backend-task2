package com.nc.task2.controller;

import com.nc.task2.dto.FileResponse;
import com.nc.task2.entity.FileEntity;
import com.nc.task2.entity.User;
import com.nc.task2.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileRepository fileRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    // ---------------- UPLOAD ----------------
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @AuthenticationPrincipal User user) throws IOException {

        // Validate file type
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.matches(".*\\.(pdf|png|jpg)$")) {
            return ResponseEntity.badRequest().body("Only PDF, PNG, JPG files are allowed.");
        }

        // Ensure upload folder exists
        Files.createDirectories(Paths.get(uploadDir));

        // Unique filename
        String uniqueName = UUID.randomUUID() + "_" + originalName;
        String filePath = uploadDir + "/" + uniqueName;

        // Save file to disk
        file.transferTo(new File(filePath));

        // Save metadata in DB
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(originalName);
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFilePath(filePath);
        fileEntity.setUploadTime(Instant.now());
        fileEntity.setUser(user);

        fileRepository.save(fileEntity);

        return ResponseEntity.ok("File uploaded successfully: " + uniqueName);
    }

    // ---------------- LIST ----------------
    @GetMapping("/list")
    public List<FileResponse> listFiles(@AuthenticationPrincipal User user) {
        return fileRepository.findByUser(user)
                .stream()
                .map(f -> new FileResponse(
                        f.getId(),
                        f.getFileName(),
                        f.getFilePath(),
                        f.getFileType(),
                        f.getUploadTime(),
                        f.getUser().getId() // only user id
                ))
                .toList();
    }

    // ---------------- DOWNLOAD ----------------
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id,
                                               @AuthenticationPrincipal User user) throws IOException {

        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Only allow owner
        if (!file.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        byte[] data = Files.readAllBytes(Paths.get(file.getFilePath()));

        return ResponseEntity.ok()
                .header("Content-Type", file.getFileType())
                .header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"")
                .body(data);
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id,
                                             @AuthenticationPrincipal User user) throws IOException {

        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Only allow owner
        if (!file.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Not allowed to delete this file");
        }

        // Delete file from disk
        Files.deleteIfExists(Paths.get(file.getFilePath()));

        // Delete metadata
        fileRepository.delete(file);

        return ResponseEntity.ok("File deleted successfully");
    }
}