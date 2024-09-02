package com.example.mds.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ClubMember { //어떤 동아리에 어떤 멤버가 가입했는지 알 수 있음.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member; //멤버

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    @JsonIgnore
    private Club club; //동아리
}
