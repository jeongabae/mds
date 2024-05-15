package com.example.mds.dto.club.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class ClubCreateRequest {
    private String name;

    private String introduce;

    private String title;

    private String explanation;

    private String location;

    private Long money;

    private String category;

    private MultipartFile image;
}
