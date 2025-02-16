package com.shefamma.shefamma.config;

import com.shefamma.shefamma.entities.AccountEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AccountEntityUserDetails implements UserDetails {

    private String name;
    private String password;
    @Getter
    private String role;

    public AccountEntityUserDetails(AccountEntity userInfo) {
        name = userInfo.getPhone();
        password = userInfo.getPassword();
        this.role = userInfo.getRole();

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
