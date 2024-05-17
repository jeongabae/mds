package com.example.mds.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PostImageService {
    @Value("${file.dir}") // application.properties에서 설정한 경로를 주입받음
    private String uploadDir;

    public String saveImage(MultipartFile file) {
        // 파일 이름 생성
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 저장할 경로 생성
        Path uploadPath = Paths.get(uploadDir);

        // 디렉토리가 존재하지 않으면 생성
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 경로를 생성할 수 없습니다.", e);
            }
        }

        // 파일 저장 경로 생성
        Path filePath = uploadPath.resolve(fileName);

        // 파일 저장
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException("이미지를 저장할 수 없습니다.", e);
        }

        return fileName;
    }

    //추가 테스트 중.
    public Resource loadImageAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Failed to load image");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to load image", e);
        }
    }
}
