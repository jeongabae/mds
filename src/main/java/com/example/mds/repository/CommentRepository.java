package com.example.mds.repository;

import com.example.mds.entity.Comment;
import com.example.mds.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시물에 딸린 댓글 삭제
    @Transactional
    void deleteByPost(Post post);

}
