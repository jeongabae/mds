//package com.example.mds.controller;
//
//import com.example.mds.dto.clubMember.request.ClubMemberJoinRequest;
//import com.example.mds.service.ClubMemberService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Tag(name = "동아리-멤버 연결 컨트롤러", description = "ClubMember Controller")
//@RequestMapping("/membership")
//@RequiredArgsConstructor
//@Controller
//public class ClubMemberController {
//    private final ClubMemberService clubMembershipService;
//
//    @Operation(summary = "동아리에 회원 가입시키기")
//    @PostMapping("/join")
//    public ResponseEntity<String> joinClub(@RequestBody ClubMemberJoinRequest request) {
//        clubMembershipService.joinClub(request);
//        return new ResponseEntity<>("Joined club successfully", HttpStatus.OK);
//    }
//}
