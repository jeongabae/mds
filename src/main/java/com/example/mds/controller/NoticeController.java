package com.example.mds.controller;

import com.example.mds.dto.notice.request.NoticeCreateRequest;
import com.example.mds.entity.Club;
import com.example.mds.entity.Notice;
import com.example.mds.service.ClubService;
import com.example.mds.service.MemberService;
import com.example.mds.service.NoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;


@Tag(name = "공지사항 컨트롤러", description = "Notice Controller")
@RequestMapping("/notice")
@RequiredArgsConstructor
@Controller
public class NoticeController {

    private final NoticeService noticeService;

    private final MemberService memberService;

    private final ClubService clubService;



    @GetMapping("/all")
    public String listNotices(@RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 8;

        Page<Notice> noticePage = noticeService.getAllNotices(PageRequest.of(page - 1, pageSize));

        model.addAttribute("notices", noticePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", noticePage.getTotalPages());

        return "noticeBasic";
    }

    @GetMapping("/{id}")
    public String getNoticeById(@PathVariable Long id, Model model) {
        Notice notice = noticeService.getNoticeById(id);
        model.addAttribute("notice", notice);
        val nlString = System.getProperty("line.separator").toString();
        model.addAttribute("nlString", nlString);
        return "noticeDetail"; // Thymeleaf 템플릿 파일 경로
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/create")
    public String createNoticeForm(Principal principal, Model model) {
        String email = principal.getName();

        List<Club> clubs = memberService.getClubsForMember(email);

        model.addAttribute("clubs", clubs);

        if (!clubs.isEmpty()) {
            Long currentClubId = clubs.get(0).getId();
            NoticeCreateRequest noticeCreateRequest = new NoticeCreateRequest();
            noticeCreateRequest.setClubId(currentClubId);
            model.addAttribute("notice", noticeCreateRequest);
        } else {
            // 사용자가 가입한 동아리가 없을 경우에 대한 처리 (예: 에러 메시지 등)
        }
        return "noticeWrite";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public String createNotice(@ModelAttribute NoticeCreateRequest request) {
        System.out.println(request);
        noticeService.createNotice(request);
        return "redirect:/notice/all";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/edit")
    public String editNoticeForm(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model
    ) {
        Notice notice = noticeService.getNoticeById(id);
        String loggedInUserEmail = userDetails.getUsername();
        Club club = notice.getClub();

        if (!clubService.isClubAdmin(club.getId(), loggedInUserEmail)) {
            return "noticeAccessDenied";
        }

        NoticeCreateRequest noticeRequest = new NoticeCreateRequest();
        noticeRequest.setTitle(notice.getTitle());
        noticeRequest.setContent(notice.getContent());
        noticeRequest.setClubId(notice.getClub().getId());

        model.addAttribute("notice", notice);
        model.addAttribute("noticeRequest", noticeRequest);
        return "noticeEdit"; // Thymeleaf 템플릿 파일 경로
    }

    @PostMapping("/{id}/edit")
    public String updateNotice(@PathVariable Long id, @ModelAttribute NoticeCreateRequest request) {
        Notice notice = noticeService.getNoticeById(id);
        request.setClubId(notice.getClub().getId());
        noticeService.updateNotice(id, request);
        return "redirect:/notice/all";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteNotice(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails
    ) {
        Notice notice = noticeService.getNoticeById(id);
        String loggedInUserEmail = userDetails.getUsername();
        Club club = notice.getClub();

        if (!clubService.isClubAdmin(club.getId(), loggedInUserEmail)) {
            return "noticeAccessDenied";
        }
        noticeService.deleteNotice(id);
        return "redirect:/notice/all";
    }

}
