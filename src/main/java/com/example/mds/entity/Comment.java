package com.example.mds.entity;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id

    @Column(columnDefinition = "TEXT")
    private String content; //댓글 내용

    private LocalDateTime createDate; //댓글 생성일자

    private LocalDateTime modifyDate; //댓글 수정일자

    @ManyToOne
    private Post post; //게시글

    @ManyToOne
    private Member author; //댓글 작성자

}