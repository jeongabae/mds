package com.example.mds.dto.comment.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateRequest {
    @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;
}
