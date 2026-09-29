package com.example.macarena_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootDir = Paths.get("uploads/products");

    public FileStorageService() {
        try {
            Files.createDirectories(rootDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String store(MultipartFile file) {
        try {
            String ext = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            Path target = rootDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/products/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public void delete(String url) {
        try {
            // url = "/uploads/products/abc.jpg" -> filename = "abc.jpg"
            String filename = url.substring(url.lastIndexOf('/') + 1);
            Path base = rootDir.toAbsolutePath().normalize();
            Path file = base.resolve(filename).normalize();
            if (file.startsWith(base)) {               // path traversal safety
                Files.deleteIfExists(file);
            }
        } catch (IOException e) {
            // file delete fail aanaalum request fail aagaadhu
            e.printStackTrace();
        }
    }

    private String getExtension(String name) {
        if (name == null || !name.contains(".")) return "";
        return name.substring(name.lastIndexOf("."));
    }
}