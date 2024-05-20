package com.example.mds.repository;

import com.example.mds.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findByCategory(String category);
    Optional<Club> findByName(String name);
}
