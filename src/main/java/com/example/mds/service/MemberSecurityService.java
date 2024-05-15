package com.example.mds.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.mds.common.MemberRole;
import com.example.mds.entity.Member;
import com.example.mds.repository.MemberRepository;
import com.example.mds.security.CustomUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
//            System.out.println("여기~~");
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        Member member = optionalMember.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getRole() == MemberRole.ADMIN) {
            authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(MemberRole.USER.getValue()));
        }
//        return new User(member.getEmail(), member.getPassword(), authorities);
        return new CustomUser(member.getEmail(), member.getPassword(), authorities,
                member.getName(), member.getStudentId(), member.getMajor());
    }
}
