package com.example.mds.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id

    @Column(columnDefinition = "TEXT")
    private String content; //내용

    private LocalDateTime createDate; //생성일자

    private LocalDateTime modifyDate; //수정일자


    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList; //게시글에 달린 댓글목록

    @ManyToOne
    private Member author; //작성자

    @ManyToOne
    private Club club; // 속한 동아리.(게시글은 하나의 동아리에 속함)

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private PostImage image; //게시글 이미지

    public String getImageFileName() {
        if (this.image != null) {
            return this.image.getStoreFileName();
        }
        return null; // 이미지가 없을 경우 null 반환
    }


}