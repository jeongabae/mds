package com.example.mds.service;


import com.example.mds.dto.notice.request.NoticeCreateRequest;
import com.example.mds.entity.Club;
import com.example.mds.entity.Member;
import com.example.mds.entity.Notice;
import com.example.mds.repository.ClubRepository;
import com.example.mds.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    private final ClubRepository clubRepository;

    private final MemberService memberService;

    @Transactional
    public Notice createNotice(NoticeCreateRequest request) {
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid club ID"));

        // 현재 로그인 중인 사용자의 이메일 가져오기
        String userEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // 현재 로그인 중인 사용자의 Member 객체 가져오기
        Member author = memberService.getMember(userEmail);

        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setClub(club);
        notice.setCreateDate(LocalDateTime.now());
        notice.setModifyDate(LocalDateTime.now());
        notice.setAuthor(author); // 현재 로그인 중인 사용자를 작성자로 설정

        return noticeRepository.save(notice);
    }

    @Transactional
    public Page<Notice> getAllNotices(Pageable pageable) {
        Pageable sortedByDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createDate").descending());
        return noticeRepository.findAll(sortedByDateDesc);
    }

    @Transactional
    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found"));
    }

    @Transactional
    public Notice updateNotice(Long id, NoticeCreateRequest request) {
        Notice notice = getNoticeById(id);
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid club ID"));

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setClub(club);
        notice.setModifyDate(LocalDateTime.now());

        return noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = getNoticeById(id);
        noticeRepository.delete(notice);
    }

    public List<Notice> getNoticesByClubId(Long clubId) {
        return noticeRepository.findByClubIdOrderByCreateDateDesc(clubId);
    }
}
