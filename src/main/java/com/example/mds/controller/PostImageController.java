package com.example.mds.controller;

import com.example.mds.service.ClubImageService;
import com.example.mds.service.PostImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "게시글 이미지 컨트롤러", description = "PostImage Controller")
@RequestMapping("/post")
@RequiredArgsConstructor
@Controller
public class PostImageController {
    private final PostImageService postImageService;

    @Operation(summary = "동아리 이미지 가져오기")
    @Parameter(name = "fileName", description = "이미지 파일 이름", required = true, example = "example.jpg", in = ParameterIn.PATH)
    @GetMapping("/image/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable("fileName") String fileName) {
        Resource resource = postImageService.loadImageAsResource(fileName);

        // 상품 이미지 파일 이름에서 확장자 추출(ex.jpg, png, heif)
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

        // 추출한 확장자에 따라 적절한 미디어 타입 설정
        MediaType mediaType;
        switch (fileExtension.toLowerCase()) {
            case "jpeg":
            case "jpg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case "heif":
                mediaType = MediaType.valueOf("image/heif");
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
