package com.example.mds.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SecurityExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        // 접근 권한에 의한 접근불가 페이지로 리디렉션
        return "noticeAccessDenied";
    }
}
