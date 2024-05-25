package com.example.mds.service;

import com.example.mds.dto.club.request.ClubCreateRequest;
import com.example.mds.entity.Club;
import com.example.mds.entity.ClubImage;
import com.example.mds.entity.Member;
import com.example.mds.handler.DataNotFoundException;
import com.example.mds.repository.ClubImageRepository;
import com.example.mds.repository.ClubMemberRepository;
import com.example.mds.repository.ClubRepository;
import com.example.mds.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClubService {

    private final ClubImageService clubImageService;
    private final ClubImageRepository clubImageRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final MemberRepository memberRepository;


    public ClubService(ClubImageService clubImageService, ClubImageRepository clubImageRepository, ClubRepository clubRepository, ClubMemberRepository clubMemberRepository, MemberRepository memberRepository) {
        this.clubImageService = clubImageService;
        this.clubImageRepository = clubImageRepository;
        this.clubRepository = clubRepository;
        this.clubMemberRepository = clubMemberRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Club registerClub(ClubCreateRequest request) {
        Club club = new Club();
        club.setName(request.getName());
        club.setIntroduce(request.getIntroduce());
        club.setTitle(request.getTitle());
        club.setExplanation(request.getExplanation());
        club.setLocation(request.getLocation());
        club.setMoney(request.getMoney());
        club.setCreateDate(LocalDateTime.now());
        club.setModifyDate(LocalDateTime.now());
        club.setCategory(request.getCategory());
        club.setApplicationFormUrl(request.getApplicationFormUrl());

        MultipartFile file = request.getImage();
        if (request.getImage() != null) { // 이미지가 제공되었는지 확인
            ClubImage clubImage = new ClubImage();
            clubImage.setUploadFileName(file.getOriginalFilename());
            clubImage.setStoreFileName(clubImageService.saveImage(file));
            clubImage.setClub(club);
            clubImageRepository.save(clubImage);
            club.setImage(clubImage);
        }

        return clubRepository.save(club);
    }

    @Transactional
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public Club getClub(Long id){
        Optional<Club> club = this.clubRepository.findById(id);
        if(club.isPresent()){
            return club.get();
        }else{
            throw new DataNotFoundException("club을 찾을 수 없습니다.");
        }
    }


    @Transactional
    public List<Club> getClubsByCategory(String category) {
        return clubRepository.findByCategory(category);
    }

    public long getClubMemberCount(Long clubId) {
        return clubMemberRepository.countByClubId(clubId);
    }

    public Club assignAdminToClub(String clubName, String memberName) {
        Club club = clubRepository.findByName(clubName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid club name: " + clubName));
        Member admin = memberRepository.findByName(memberName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member name: " + memberName));
        club.setAdmin(admin);
        return clubRepository.save(club);
    }

}
