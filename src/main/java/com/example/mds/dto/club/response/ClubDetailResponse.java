package com.example.mds.dto.club.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class ClubDetailResponse {
    private String name;

    private String introduce;

    private String title;

    private String explanation;

    private String location;

    private Long money;

    private String category;

    private String imageFileName;

//    private MultipartFile image;
}
