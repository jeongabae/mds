package com.example.mds.service;

import com.example.mds.entity.Member;
import com.example.mds.entity.Post;
import com.example.mds.handler.DataNotFoundException;
import com.example.mds.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public Page<Post> getPostList(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 12, Sort.by(sorts));
        return this.postRepository.findAll(pageable);
    }

    public Post getPost(Long id){
        Optional<Post> post = this.postRepository.findById(id);
        if(post.isPresent()){
            return post.get();
        }else{
            throw new DataNotFoundException("post를 찾을 수 없습니다.");
        }
    }

    public void create(String content, Member author){
        Post p = new Post();
        p.setContent(content);
        p.setCreateDate(LocalDateTime.now());
        p.setAuthor(author);
        this.postRepository.save(p);
    }

    public void modify(Post post, String content){
        post.setContent(content);
        post.setModifyDate(LocalDateTime.now());
        this.postRepository.save(post);
    }

    public void delete(Post post){
        this.postRepository.delete(post);
    }

}
