package com.example.mds.security;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUser extends User {

    private final String name;
    private final Long studentId;
    private final String major;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                      String name, Long studentId, String major) {
        super(username, password, authorities);
        this.name = name;
        this.studentId = studentId;
        this.major = major;

    }
}
