package com.example.mds.controller;

import com.example.mds.dto.club.response.ClubDetailResponse;
import com.example.mds.dto.club.response.MyClubsResponse;
import com.example.mds.dto.member.request.MemberCreateRequest;
import com.example.mds.dto.member.request.MemberUpdateRequest;
import com.example.mds.entity.Club;
import com.example.mds.entity.Member;
import com.example.mds.security.CustomUser;
import com.example.mds.service.MemberSecurityService;
import com.example.mds.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Tag(name = "멤버 컨트롤러", description = "Member Controller")
@RequestMapping("/member")
@RequiredArgsConstructor
@Controller
public class MemberController {
    private final MemberService memberService;
    private final MemberSecurityService memberSecurityService;

    @GetMapping("/signup")
    public String signup(MemberCreateRequest memberCreateRequest) {
            return "signup";
    }


    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public String signup(@Valid MemberCreateRequest memberCreateRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "signup";
        }
        if (!memberCreateRequest.getPassword1().equals(memberCreateRequest.getPassword2())){
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
            return "signup";
        }
        try {
            memberService.create(memberCreateRequest.getEmail(), memberCreateRequest.getName(),
                    memberCreateRequest.getPassword1(), memberCreateRequest.getStudentId(), memberCreateRequest.getMajor());
        }catch(DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup";
        } catch(Exception e){
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
//
//    @GetMapping("/mypage")
//    public String mypage(){
//        return "myPage";
//    }

    @GetMapping("/mypage")
    public String mypage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        List<Club> clubs = memberService.getClubsForMember(userEmail);
        List<MyClubsResponse> myClubsResponses = clubs.stream().map(club -> {
            MyClubsResponse response = new MyClubsResponse(
                    club.getId(),
                    club.getName(),
                    club.getImageFileName()
            );
            return response;
        }).collect(Collectors.toList());
        model.addAttribute("clubs", myClubsResponses);
        return "myPage";
    }

    @GetMapping("/update")
    public String showUpdateForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        Member member = memberService.getMember(userEmail); // 이메일을 기반으로 회원 정보 조회
        MemberUpdateRequest updateRequest = new MemberUpdateRequest();
        updateRequest.setEmail(member.getEmail());
        updateRequest.setName(member.getName());
        updateRequest.setStudentId(member.getStudentId());
        updateRequest.setMajor(member.getMajor());
        // 이 외에도 필요한 정보들을 updateRequest에 세팅해줍니다.
        model.addAttribute("updateRequest", updateRequest);
        return "updateProfile";
    }

    @Operation(summary = "회원정보 업데이트")
    @PostMapping("/update")
    public String updateMember(@Valid @ModelAttribute("updateRequest") MemberUpdateRequest updateRequest,
                               BindingResult bindingResult) {
        if (!updateRequest.getPassword().equals(updateRequest.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordMismatch", "비밀번호가 일치하지 않습니다.");
            return "updateProfile";
        }
        if (bindingResult.hasErrors()) {
            return "updateProfile";
        }
        memberService.update(updateRequest);

        // 업데이트된 사용자 정보로 새로운 CustomUserDetails 생성
        CustomUser updatedUser = new CustomUser(updateRequest.getEmail(), updateRequest.getPassword(),
                memberSecurityService.loadUserByUsername(updateRequest.getEmail()).getAuthorities(),
                updateRequest.getName(), updateRequest.getStudentId(), updateRequest.getMajor());

        // SecurityContext에 새로운 Authentication 설정
        Authentication auth = new UsernamePasswordAuthenticationToken(updatedUser, null, updatedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/member/mypage";
    }

    // 회원 탈퇴 처리
    @Operation(summary = "회원 탈퇴")
    @PostMapping("/withdraw")
    public String withdrawMember(Authentication authentication) {
        CustomUser userDetails = (CustomUser) authentication.getPrincipal();
        memberService.deleteMember(userDetails.getUsername());
        return "redirect:/member/logout"; // 로그아웃 페이지로 리다이렉트 또는 다른 페이지로 이동
    }

    @Operation(summary = "동아리에 회원 가입시키기")
    @PostMapping("/join")
    public ResponseEntity<String> joinClub(@RequestParam Long studentId, @RequestParam Long clubId) {
        memberService.joinClub(studentId, clubId);
        return new ResponseEntity<>("Joined club successfully", HttpStatus.OK);
    }

    @Operation(summary = "동아리에서 회원 탈퇴시키기")
    @PostMapping("/leave")
    public ResponseEntity<String> leaveClub(@RequestParam Long studentId, @RequestParam Long clubId) {
        memberService.leaveClub(studentId, clubId);
        return new ResponseEntity<>("Left club successfully", HttpStatus.OK);
    }

    @Operation(summary = "회원이 가입한 동아리 목록 가져오기")
    @GetMapping("/{studentId}/clubs")
    public ResponseEntity<List<Club>> getClubsForMember(@PathVariable String email) {
        List<Club> clubs = memberService.getClubsForMember(email);
        return new ResponseEntity<>(clubs, HttpStatus.OK);
    }

}
