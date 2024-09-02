package com.example.mds.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class ClubImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id

    private String uploadFileName; //업로드 파일명

    private String storeFileName; //저장된 파일명

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    @JsonIgnore
    private Club club; //동아리명


    public ClubImage(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
