//package com.example.mds.service;
//
//import com.example.mds.dto.clubMember.request.ClubMemberJoinRequest;
//import com.example.mds.entity.Club;
//import com.example.mds.entity.ClubMember;
//import com.example.mds.entity.Member;
//import com.example.mds.repository.ClubMemberRepository;
//import com.example.mds.repository.MemberRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ClubMemberService {
//    private final MemberRepository memberRepository;
//    private final ClubMemberRepository clubMemberRepository;
//
//    public ClubMemberService(MemberRepository memberRepository, ClubMemberRepository clubMemberRepository) {
//        this.memberRepository = memberRepository;
//        this.clubMemberRepository = clubMemberRepository;
//    }
//
//    @Transactional
//    public void joinClub(ClubMemberJoinRequest request) {
//        Member member = memberRepository.findByStudentId(request.getStudentId());
//        if (member == null) {
//            throw new IllegalArgumentException("Member with studentId " + request.getStudentId() + " not found");
//        }
//
//        ClubMember clubMember = new ClubMember();
//        Club club = new Club();
//        club.setId(request.getClubId());
//
//        clubMember.setMember(member);
//        clubMember.setClub(club);
//
//        clubMemberRepository.save(clubMember);
//    }
//}
