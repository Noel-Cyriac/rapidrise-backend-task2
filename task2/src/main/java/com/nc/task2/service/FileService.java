package com.nc.task2.service;

import com.nc.task2.entity.FileEntity;
import com.nc.task2.entity.User;
import com.nc.task2.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final String uploadDir = "uploads/";

    public void store(MultipartFile file, User user) throws IOException {
        File dir = new File(uploadDir); if(!dir.exists()) dir.mkdir();
        String filename = UUID.randomUUID()+"_"+file.getOriginalFilename();
        Path path = Paths.get(uploadDir + filename);
        Files.copy(file.getInputStream(), path);
        FileEntity entity = new FileEntity();
        entity.setFileName(filename);
        entity.setFilePath(path.toString());
        entity.setUser(user);
        fileRepository.save(entity);
    }
}
