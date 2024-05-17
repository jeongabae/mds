package com.example.mds.repository;

import com.example.mds.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    long countByClubId(Long clubId);
}
