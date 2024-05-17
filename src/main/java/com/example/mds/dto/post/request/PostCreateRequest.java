package com.example.mds.dto.post.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostCreateRequest {
    @NotEmpty(message="내용은 필수 항목입니다.")
    private String content;

    private Long clubId;

    private MultipartFile image;
}
