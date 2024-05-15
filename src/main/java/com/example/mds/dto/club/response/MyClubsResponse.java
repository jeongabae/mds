package com.example.mds.dto.club.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyClubsResponse {
    private Long id;
    private String name;
    private String imageFileName;
}
