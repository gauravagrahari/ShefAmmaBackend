package com.shefamma.shefamma.config;

import com.shefamma.shefamma.entities.GuestAccountEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public class GuestAccountEntityUserDetails implements UserDetails {


    private String name;
    private String password;

    public GuestAccountEntityUserDetails(GuestAccountEntity guestAccount) {
        name=guestAccount.getGuestPhone();
        password=guestAccount.getPassword();
    }


    public void GuestAccountEntityUserDetails(GuestAccountEntity userInfo) {
        name=userInfo.getGuestPhone();
        password=userInfo.getPassword();
//        authorities= Arrays.stream(userInfo.getRoles().split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
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
