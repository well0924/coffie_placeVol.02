package com.example.coffies_vol_02.config.security.auth;

import com.example.coffies_vol_02.member.domain.Member;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.Proxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Log4j2
@Getter
@Proxy(lazy = false)
public class CustomUserDetails implements UserDetails,Serializable {
    private Member member;
    
    public CustomUserDetails(Member member){
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.info("CustomUserDetail....");

        Collection<GrantedAuthority> collectors = new ArrayList<>();
        collectors.add(()-> member.getRole().getValue());
        return collectors;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    //계정 잠금
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

