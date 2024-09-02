package com.example.mds.security;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUser extends User {

    private final String name; //이름
    private final Long studentId; //학번
    private final String major; //학과

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                      String name, Long studentId, String major) {
        super(username, password, authorities);
        this.name = name;
        this.studentId = studentId;
        this.major = major;

    }
}
