package com.example.mds.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String introduce;

    @Column(nullable = false)
    private String name;


    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private String location;

    private Long money;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @Column(nullable = false)
    private String category;

    @OneToOne(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClubImage image;

    public String getImageFileName() {
        if (this.image != null) {
            return this.image.getStoreFileName();
        }
        return null; // 이미지가 없을 경우 null 반환
    }

    @OneToMany(mappedBy = "club")
    private List<ClubMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "club")
    private List<Post> posts = new ArrayList<>();
}
