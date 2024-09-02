package com.example.mds.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id

    @Column(length = 200)
    private String title; //제목

    @Column(columnDefinition = "TEXT")
    private String content; //설명

    private LocalDateTime createDate; //생성일자

    private LocalDateTime modifyDate; //수정일자

    @ManyToOne
    private Club club; //동아리

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author; //작성자
}
