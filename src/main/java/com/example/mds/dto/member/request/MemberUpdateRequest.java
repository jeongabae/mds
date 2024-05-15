package com.example.mds.dto.member.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateRequest {
    private String email;
    private String name;
    private String password;
    private String password2;
    private Long studentId;
    private String major;
}
