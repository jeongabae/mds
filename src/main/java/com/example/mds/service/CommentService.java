package com.example.mds.service;

import com.example.mds.entity.Comment;
import com.example.mds.entity.Member;
import com.example.mds.entity.Post;
import com.example.mds.handler.DataNotFoundException;
import com.example.mds.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public void create(Post post, String content, Member author){
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setPost(post);
        comment.setAuthor(author);
        this.commentRepository.save(comment);
    }

    public Comment getComment(Long id){
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()){
            return comment.get();
        }else{
            throw new DataNotFoundException("댓글을 찾을 수 없습니다.");
        }
    }

    public void modify(Comment comment, String content){
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }
}
