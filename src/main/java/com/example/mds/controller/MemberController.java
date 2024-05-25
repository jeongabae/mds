package com.example.mds.controller;

import com.example.mds.common.MemberRole;
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

import java.util.Arrays;
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
    public String signup(Model model,MemberCreateRequest memberCreateRequest) {
        model.addAttribute("majors", Arrays.asList("컴퓨터정보통신공학부", "소프트웨어학부", "경영학부", "디자인예술학부", "자율융합학부", "디지털헬스케어학부", "물리치료학과", "작업치료학과"));
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

    @Operation(summary = "관리자 회원가입")
    @PostMapping("/signup-admin")
    public ResponseEntity<Member> createClubAdmin(@RequestBody MemberCreateRequest memberCreateRequest, BindingResult bindingResult) {
        if (!memberCreateRequest.getPassword1().equals(memberCreateRequest.getPassword2())){
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
        }
        Member createdMember = memberService.createClubAdmin(
                memberCreateRequest.getEmail(),
                memberCreateRequest.getName(),
                memberCreateRequest.getPassword1(),
                memberCreateRequest.getStudentId(),
                memberCreateRequest.getMajor()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
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
        if (authentication == null || !authentication.isAuthenticated()) {
            // 인증되지 않은 사용자의 경우 처리
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
        String userEmail = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기
        MemberRole memberRole = memberService.getRoleByEmail(userEmail); //권한에 따라 다른 마이페이지 화면 보이게 하기

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
        model.addAttribute("memberRole", memberRole.getValue());
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
//    @Operation(summary = "회원 탈퇴")
//    @PostMapping("/withdraw")
//    public String withdrawMember(Authentication authentication) {
//        CustomUser userDetails = (CustomUser) authentication.getPrincipal();
//        memberService.deleteMember(userDetails.getUsername());
//        return "redirect:/member/logout"; // 로그아웃 페이지로 리다이렉트 또는 다른 페이지로 이동
//    }
//    @Operation(summary = "회원 탈퇴")
//    @DeleteMapping("/withdraw")
//    public String withdrawMember(Authentication authentication) {
//        CustomUser userDetails = (CustomUser) authentication.getPrincipal();
//        memberService.deleteMember(userDetails.getUsername());
//        return "redirect:/member/logout"; // 로그아웃 페이지로 리다이렉트 또는 다른 페이지로 이동
//    }
    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdrawMember(Authentication authentication) {
        CustomUser userDetails = (CustomUser) authentication.getPrincipal();
        memberService.deleteMember(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "동아리에 회원 가입시키기")
    @PostMapping("/join")
    public ResponseEntity<String> joinClub(@RequestParam("studentId") Long studentId, @RequestParam("clubId") Long clubId) {
        memberService.joinClub(studentId, clubId);
        return new ResponseEntity<>("Joined club successfully", HttpStatus.OK);
//        return "redirect:/club/" + clubId;
    }

    @Operation(summary = "동아리에서 회원 탈퇴시키기")
    @PostMapping("/leave")
    public ResponseEntity<String> leaveClub(@RequestParam("studentId") Long studentId, @RequestParam("clubId") Long clubId) {
        memberService.leaveClub(studentId, clubId);
        return new ResponseEntity<>("Left club successfully", HttpStatus.OK);
    }

    @Operation(summary = "회원이 가입한 동아리 목록 가져오기")
    @GetMapping("/{email}/clubs")
    public ResponseEntity<List<Club>> getClubsForMember(@PathVariable("email") String email) {
        List<Club> clubs = memberService.getClubsForMember(email);
        return new ResponseEntity<>(clubs, HttpStatus.OK);
    }


}
