package com.example.mds.repository;

import com.example.mds.entity.Comment;
import com.example.mds.entity.Member;
import com.example.mds.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시물에 딸린 댓글 삭제
    @Transactional
    void deleteByPost(Post post);

    //게시물에 있는 댓글 모두 찾기
    List<Comment> findByPost(Post post);

    List<Comment> findByAuthor(Member author);


}
