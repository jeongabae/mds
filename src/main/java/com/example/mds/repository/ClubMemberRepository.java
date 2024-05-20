package com.example.mds.repository;

import com.example.mds.entity.Club;
import com.example.mds.entity.ClubMember;
import com.example.mds.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    long countByClubId(Long clubId);
    Optional<ClubMember> findByMemberAndClub(Member member, Club club);
}
