package com.example.mds;

import com.example.mds.entity.Post;
import com.example.mds.repository.PostRepository;
import com.example.mds.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MdsApplicationTests {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;
    @Test
    void contextLoads() {
//        Post p1 = new Post();
//        p1.setTitle("첫번째 게시물");
//        p1.setContent("게시물 테스트 중입니다.");
//        p1.setCreateDate(LocalDateTime.now());
//        this.postRepository.save(p1);
//
//        Post p2 = new Post();
//        p2.setContent("게시물 테스트 중입니다.2");
//        p2.setCreateDate(LocalDateTime.now());
//        this.postRepository.save(p2);

//        List<Post> all = this.postRepository.findAll();
//        assertEquals(6, all.size());
//        Post p = all.get(0);
//        assertEquals("첫번째 게시물", p.getTitle());
//        for(int i=1;i<=300;i++){
//            String content = String.format("테스트 데이터입니다.:[%03d]", i);
//            this.postService.create(content, null);
//        }
    }

}
