package com.example.mds.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id

    private String introduce; //동아리 표어

    @Column(nullable = false)
    private String name; //동아리명


    @Column(length = 200)
    private String title; //동아리 짧은 소개

    @Column(columnDefinition = "TEXT")
    private String explanation; //동아리 설명

    private String location; //동방 위치

    private Long money; //동아리 회비

    private LocalDateTime createDate; //동아리 생성일자

    private LocalDateTime modifyDate; //동아리 수정일자

    @Column(nullable = false)
    private String category; //동아리 카테고리

    private String applicationFormUrl; //동아리 지원폼

    @OneToOne(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private ClubImage image; //동아리 로고


    @OneToMany(mappedBy = "club")
    @JsonIgnore
    private List<ClubMember> members = new ArrayList<>(); //동아리 가입한 멤버

    @OneToMany(mappedBy = "club")
    @JsonIgnore
    private List<Post> posts = new ArrayList<>(); //동아리 게시글

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    @JsonIgnore
    private Member admin; //동아리 관리자

    public String getImageFileName() {
        if (this.image != null) {
            return this.image.getStoreFileName();
        }
        return null; // 이미지가 없을 경우 null 반환
    }


}
