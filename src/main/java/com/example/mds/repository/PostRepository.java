package com.example.mds.repository;

import com.example.mds.entity.Member;
import com.example.mds.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);

    // 특정 회원이 작성한 게시물 조회
    List<Post> findByAuthor(Member author);

    Page<Post> findByClubCategory(String category, Pageable pageable);
}