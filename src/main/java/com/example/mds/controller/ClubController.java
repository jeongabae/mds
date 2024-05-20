package com.example.mds.controller;

import com.example.mds.dto.club.request.ClubCreateRequest;
import com.example.mds.dto.club.response.ClubDetailResponse;
import com.example.mds.dto.comment.request.CommentCreateRequest;
import com.example.mds.entity.Club;
import com.example.mds.entity.Post;
import com.example.mds.repository.ClubRepository;
import com.example.mds.repository.PostRepository;
import com.example.mds.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "동아리 컨트롤러", description = "Club Controller")
@RequestMapping("/club")
@RequiredArgsConstructor
@Controller
public class ClubController {
    private final ClubService clubService;
    private final ClubRepository clubRepository;

    @Operation(summary = "동아리 등록")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Club> registerClub(@ModelAttribute ClubCreateRequest request) {
        Club club = clubService.registerClub(request);
        return new ResponseEntity<>(club, HttpStatus.CREATED);
    }

    @Operation(summary = "모든 동아리 조회")
    @GetMapping
    public ResponseEntity<List<ClubDetailResponse>> getAllClubs() {
        List<Club> clubList = clubService.getAllClubs(); // 모든 상품을 가져옵니다.
        List<ClubDetailResponse> clubResponseList = clubList.stream().map(club -> {
            ClubDetailResponse response = new ClubDetailResponse(

                    club.getName(),
                    club.getIntroduce(),
                    club.getTitle(),
                    club.getExplanation(),
                    club.getLocation(),
                    club.getMoney(),
                    club.getCategory(),
                    club.getImageFileName()

            );
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(clubResponseList);
    }

//    @GetMapping("/all")
//    public String list(Model model){
//        List<Club> clubList = this.clubService.getAllClubs();
//        model.addAttribute("clubList", clubList);
//        return "clubAll";
//    }

//    @GetMapping("/category/{category}")
//    public String getClubsByCategory(@PathVariable("category") String category, Model model) {
//        List<Club> clubList = clubService.getClubsByCategory(category);
//        model.addAttribute("category", category);
//        model.addAttribute("clubList", clubList);
//        return "clubAll";
//    }

    @Operation(summary = "카테고리별 동아리 조회")
    @GetMapping("/category/{category}")
    public String getClubsByCategory(@PathVariable("category") String category, Model model) {
        List<Club> clubList = "all".equals(category)
                ? this.clubService.getAllClubs()
                : this.clubService.getClubsByCategory(category);
        model.addAttribute("category", "all".equals(category) ? null : category);
        model.addAttribute("clubList", clubList);

        return "clubAll";
    }

    @GetMapping("/{id}")
    public String detail(Model model, @PathVariable("id") Long id){
        Club club = this.clubService.getClub(id);
        long memberCount = clubService.getClubMemberCount(id);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("club",club);
        return "clubDetail";
    }

    @Operation(summary = "동아리장 설정")
    @PostMapping("/{clubName}/assign-admin/{memberName}")
    public ResponseEntity<Club> assignAdminToClub(@PathVariable("clubName") String clubName, @PathVariable("memberName") String memberName) {
        Club updatedClub = clubService.assignAdminToClub(clubName, memberName);
        return ResponseEntity.ok(updatedClub);
    }


}
