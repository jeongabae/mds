package com.example.mds.service;

import com.example.mds.dto.club.request.ClubCreateRequest;
import com.example.mds.dto.post.request.PostCreateRequest;
import com.example.mds.dto.post.request.PostUpdateRequest;
import com.example.mds.entity.*;
import com.example.mds.handler.DataNotFoundException;
import com.example.mds.repository.ClubRepository;
import com.example.mds.repository.CommentRepository;
import com.example.mds.repository.PostImageRepository;
import com.example.mds.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final ClubService clubService;
    private final PostImageService postImageService;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;

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

//    public void create(String content, Member author, Long clubId){
//        Club club = clubService.getClub(clubId);
//        Post p = new Post();
//        p.setContent(content);
//        p.setCreateDate(LocalDateTime.now());
//        p.setAuthor(author);
//        p.setClub(club);
//        this.postRepository.save(p);
//    }

    @Transactional
    public Post registerPost(PostCreateRequest request, Member author) {
        Club club = clubService.getClub(request.getClubId());
        Post p = new Post();
        p.setContent(request.getContent());
        p.setCreateDate(LocalDateTime.now());
        p.setAuthor(author);
        p.setClub(club);


        MultipartFile file = request.getImage();
        if (request.getImage() != null) { // 이미지가 제공되었는지 확인
            PostImage postImage = new PostImage();
            postImage.setUploadFileName(file.getOriginalFilename());
            postImage.setStoreFileName(postImageService.saveImage(file));
            postImage.setPost(p);
            postImageRepository.save(postImage);
            p.setImage(postImage);
        }

        return postRepository.save(p);
    }

//    public void modify(Post post, String content){
//        post.setContent(content);
//        post.setModifyDate(LocalDateTime.now());
//        this.postRepository.save(post);
//    }
    public void modify(Post post, PostUpdateRequest request){
        Club club = clubService.getClub(request.getClubId());
        post.setContent(request.getContent());
        post.setClub(club);
        // 다른 필드들도 필요에 따라 업데이트

        MultipartFile file = request.getImage();
        if (file != null) { // 이미지가 제공되었는지 확인
            PostImage postImage = post.getImage();
            if (postImage == null) {
                postImage = new PostImage();
            }
            postImage.setUploadFileName(file.getOriginalFilename());
            postImage.setStoreFileName(postImageService.saveImage(file));
            postImage.setPost(post);
            postImageRepository.save(postImage);
            post.setImage(postImage);
        }

        // 3. 변경된 내용 저장
        postRepository.save(post);

    }

    public void delete(Post post){
        PostImage postImage = post.getImage();
        // 연결된 모든 ProductImage 엔터티 삭제
        postImageRepository.delete(postImage);

        List<Comment> comments = commentRepository.findByPost(post);
        // 연결된 모든 Favorite 엔터티 삭제
        commentRepository.deleteAll(comments);
        this.postRepository.delete(post);
    }

}
