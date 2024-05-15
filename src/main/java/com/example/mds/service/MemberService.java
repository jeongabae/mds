package com.example.mds.service;

import com.example.mds.common.MemberRole;
import com.example.mds.dto.member.request.MemberUpdateRequest;
import com.example.mds.entity.Club;
import com.example.mds.entity.ClubMember;
import com.example.mds.entity.Member;
import com.example.mds.entity.Post;
import com.example.mds.handler.DataNotFoundException;
import com.example.mds.repository.ClubRepository;
import com.example.mds.repository.CommentRepository;
import com.example.mds.repository.MemberRepository;
import com.example.mds.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ClubRepository clubRepository;


    public Member create(String email, String name, String password, Long studentId, String major){
        Member member = new Member();
        member.setEmail(email);
        member.setName(name);
        member.setPassword(passwordEncoder.encode(password));
        member.setStudentId(studentId);
        member.setMajor(major);
        member.setRole(MemberRole.USER);
        this.memberRepository.save(member);
        return member;
    }

    public Member getMember(String email){
        Optional<Member> member = this.memberRepository.findByEmail(email);
        if (member.isPresent()){
            return member.get();
        }else{
            throw new DataNotFoundException("사용자 데이터를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void update(MemberUpdateRequest updateRequest) {
        Member member = memberRepository.findByEmail(updateRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Member not found with email: " + updateRequest.getEmail()));

        member.setName(updateRequest.getName());
        member.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        member.setStudentId(updateRequest.getStudentId());
        member.setMajor(updateRequest.getMajor());

        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found with email: " +email));

        // 회원이 작성한 게시물 삭제
        List<Post> posts = postRepository.findByAuthor(member);
        for (Post post : posts) {
            // 게시물에 달린 댓글 삭제
            commentRepository.deleteByPost(post);
            postRepository.delete(post);
        }

        // 회원 삭제
        memberRepository.delete(member);
    }

    @Transactional
    public void joinClub(Long studentId, Long clubId) {
        Member member = memberRepository.findByStudentId(studentId)
         .orElseThrow(() -> new RuntimeException("Member not found with student ID: " + studentId));


        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with ID: " + clubId));

        ClubMember clubMember = new ClubMember();
        clubMember.setMember(member);
        clubMember.setClub(club);
        club.getMembers().add(clubMember);

        memberRepository.save(member);
        clubRepository.save(club);
    }

    @Transactional
    public void leaveClub(Long studentId, Long clubId) {
        Member member = memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Member not found with student ID: " + studentId));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with ID: " + clubId));

        club.getMembers().removeIf(cm -> cm.getMember().getStudentId().equals(studentId));

        memberRepository.save(member);
        clubRepository.save(club);
    }

    public List<Club> getClubsForMember(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found with student ID: " + email));

        return member.getClubs().stream().map(ClubMember::getClub).collect(Collectors.toList());
    }

}
