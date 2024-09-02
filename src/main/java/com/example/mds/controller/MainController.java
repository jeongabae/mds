package com.example.mds.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "메인 컨트롤러", description = "Main Controller")
@Controller
public class MainController {
    @Operation(summary = "about 페이지")
    @GetMapping("/about")
    public String about(){
        return "about";
    }

    @Operation(summary = "contact 페이지")
    @GetMapping("/contact")
    public String contact(){
        return "contact";
    }

}
