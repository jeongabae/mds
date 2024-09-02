package com.example.mds.dto.notice.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeCreateRequest {

    @NotEmpty(message="내용은 필수 항목입니다.")
    private String content;

    @NotEmpty(message="제목은 필수 항목입니다.")
    private String title;

    private Long clubId;
}
