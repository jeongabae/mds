package com.example.mds.controller;

import com.example.mds.common.MemberRole;
import com.example.mds.dto.comment.request.CommentCreateRequest;
import com.example.mds.dto.post.request.PostCreateRequest;
import com.example.mds.dto.post.request.PostUpdateRequest;
import com.example.mds.entity.Club;
import com.example.mds.entity.Member;
import com.example.mds.entity.Post;
import com.example.mds.service.ClubService;
import com.example.mds.service.MemberService;
import com.example.mds.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Tag(name = "게시글 컨트롤러", description = "Club Controller")
@RequestMapping("/community")
@RequiredArgsConstructor
@Controller
public class PostController {
    private final PostService postService;
    private final MemberService memberService;
    private final ClubService clubService;

    @Operation(summary = "커뮤니티 리스트 페이지")
    @GetMapping("/all")
    public String list(Model model, @RequestParam(value="page", defaultValue = "0") int page){
        Page<Post> paging = this.postService.getPostList(page);
        model.addAttribute("paging", paging);
        return "communityAll";
    }

    @Operation(summary = "커뮤니티 카테고리 별 페이지")
    @GetMapping("/category/{category}")
    public String getPosts(@PathVariable(name = "category", required = false) String category,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           Model model) {
        Page<Post> paging;
        if ("all".equals(category)) {
            paging = this.postService.getPostList(page);
        } else {
            paging = this.postService.getPostsByClubCategory(category, page);
        }
        model.addAttribute("paging", paging);
        return "communityAll";
    }

    @Operation(summary = "커뮤니티 상세 페이지")
    @GetMapping("/{id}")
    public String detail(Model model, @PathVariable("id") Long id, CommentCreateRequest commentCreateRequest){
        Post post = this.postService.getPost(id);
        model.addAttribute("post",post);
        return "communityDetail";
    }

    @Operation(summary = "게시글 작성 페이지")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String postCreate(PostCreateRequest postCreateRequest, Principal principal, Model model){
        // 현재 로그인한 사용자 정보를 가져오기
        String email = principal.getName();
        MemberRole memberRole = memberService.getRoleByEmail(email); //권한에 따라 다른 게시물 작성 화면 보이게 하기

        // 현재 사용자가 가입한 동아리 목록을 조회
        List<Club> clubs = memberService.getClubsForMember(email);

        // 조회된 동아리 목록을 모델에 추가하여 뷰로 전달
        model.addAttribute("clubs", clubs);
        model.addAttribute("memberRole", memberRole.getValue());
        return "communityForm";
    }

    @Operation(summary = "게시글 작성")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="/create" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postCreate(@Valid PostCreateRequest postCreateRequest,
                             BindingResult bindingResult, Principal principal, Model model){
        if (bindingResult.hasErrors() || postCreateRequest.getClubId() == null || postCreateRequest.getContent() == null) {
            // 현재 로그인한 사용자 정보를 가져오기
            String email = principal.getName();
            MemberRole memberRole = memberService.getRoleByEmail(email); //권한에 따라 다른 게시물 작성 화면 보이게 하기

            // 현재 사용자가 가입한 동아리 목록을 조회
            List<Club> clubs = memberService.getClubsForMember(email);

            // 조회된 동아리 목록을 모델에 추가하여 뷰로 전달
            model.addAttribute("clubs", clubs);
            model.addAttribute("memberRole", memberRole.getValue());
        }
        if (bindingResult.hasErrors()){
            return "communityForm";
        }
        if (postCreateRequest.getClubId()==null){
            bindingResult.reject("errorClubId", "동아리를 선택하지 않으셨습니다.");
            return "communityForm";
        }
        if (postCreateRequest.getContent()==null){
            bindingResult.reject("errorContent", "내용을 입력하지 않으셨습니다.");
            return "communityForm";
        }

        Member member = this.memberService.getMember(principal.getName());
        this.postService.registerPost(postCreateRequest, member);

        return "redirect:/community/all";
    }

    @Operation(summary = "게시글 수정 페이지")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String postModify(Model model, PostUpdateRequest postUpdateRequest, @PathVariable("id") Long id, Principal principal){
        // 현재 로그인한 사용자의 이메일을 가져옴
        String email = principal.getName();

        // Member 엔티티를 이메일을 기반으로 데이터베이스에서 찾음
        Member member = memberService.getMember(email);

        // 현재 사용자가 가입한 동아리 목록을 조회
        List<Club> clubs = memberService.getClubsForMember(email);

        MemberRole memberRole = memberService.getRoleByEmail(email); //권한에 따라 다른 게시물 작성 화면 보이게 하기

        // 조회된 동아리 목록을 모델에 추가하여 뷰로 전달
        model.addAttribute("clubs", clubs);

        // 사용자의 이름을 모델에 추가
        model.addAttribute("name", member.getName());

        model.addAttribute("memberRole", memberRole.getValue());


        Post post = this.postService.getPost(id);
        if(!post.getAuthor().getEmail().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 게시물 수정 권한이 없습니다.");
        }
        postUpdateRequest.setContent(post.getContent());
        postUpdateRequest.setClubId(post.getClub().getId());
        model.addAttribute("name", member.getName());
        model.addAttribute("post", post);
        return "communityModifyForm";
    }

    @Operation(summary = "게시글 수정")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String postModify(@Valid PostUpdateRequest postUpdateRequest,
                             BindingResult bindingResult, @PathVariable("id") Long id, Principal principal, Model model){
        if (bindingResult.hasErrors() || postUpdateRequest.getClubId() == null || postUpdateRequest.getContent() == null) {
            // 현재 로그인한 사용자 정보를 가져오기
            String email = principal.getName();
            MemberRole memberRole = memberService.getRoleByEmail(email); //권한에 따라 다른 게시물 작성 화면 보이게 하기

            // 현재 사용자가 가입한 동아리 목록을 조회
            List<Club> clubs = memberService.getClubsForMember(email);

            // 조회된 동아리 목록을 모델에 추가하여 뷰로 전달
            model.addAttribute("clubs", clubs);
            model.addAttribute("memberRole", memberRole.getValue());
        }
        if (bindingResult.hasErrors()){
            return "communityModifyForm";
        }

        if (postUpdateRequest.getClubId()==null){
            bindingResult.reject("errorClubId", "동아리를 선택하지 않으셨습니다.");
            return "communityModifyForm";
        }
        if (postUpdateRequest.getContent()==null){
            bindingResult.reject("errorContent", "내용을 입력하지 않으셨습니다.");
            return "communityModifyForm";
        }

        Post post = this.postService.getPost(id);
        if(!post.getAuthor().getEmail().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 게시물 수정 권한이 없습니다.");
        }
        this.postService.modify(post, postUpdateRequest);
        return String.format("redirect:/community/%s",id);
    }


    @Operation(summary = "게시글 삭제")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String postDelete(Principal principal, @PathVariable("id") Long id){
        Post post = this.postService.getPost(id);
        if(!(post.getAuthor().getEmail().equals(principal.getName()) || post.getClub().getAdmin().getEmail().equals(principal.getName()))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 게시물 삭제 권한이 없습니다.");
        }
        this.postService.delete(post);
        return "redirect:/community/all";
    }

}
