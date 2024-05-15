package com.example.mds.dto.clubMember.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClubMemberJoinRequest {
    private Long studentId; // 학번
    private Long clubId;
}
