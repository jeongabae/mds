package com.example.mds.entity;

import com.example.mds.common.MemberRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id

    @Column(nullable = false, unique = true)
    private String email; //이메일

    @Column(nullable = false)
    private String name; //이름

    @Column(nullable = false)
    private String password; //비밀번호(암호화됩니다.)

    @Column(nullable = false, unique = true)
    private Long studentId; //학번

    @Column(nullable = false)
    private String major; //전공

    @Enumerated(EnumType.STRING)
    private MemberRole role; //관리자, 일반 유저 가능

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<ClubMember> clubs = new ArrayList<>(); //가입한 동아리
}
