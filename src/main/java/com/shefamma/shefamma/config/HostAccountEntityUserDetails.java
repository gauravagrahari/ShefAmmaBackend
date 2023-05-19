package com.shefamma.shefamma.config;

import com.shefamma.shefamma.entities.HostAccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class HostAccountEntityUserDetails implements UserDetails {

    private String name;
    private String password;

    public HostAccountEntityUserDetails(HostAccountEntity userInfo) {
        name = userInfo.getHostPhone();
        password = userInfo.getPassword();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // No authorities to return, so return an empty collection
        return Collections.emptyList();
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return name;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
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
