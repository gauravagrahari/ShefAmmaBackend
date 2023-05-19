package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.GuestAccountEntity;
import com.shefamma.shefamma.entities.GuestEntity;
import org.springframework.http.ResponseEntity;

public interface GuestAccount {
    ResponseEntity<GuestAccountEntity>  saveGuestSignup(GuestAccountEntity guestEntity);
    ResponseEntity<GuestAccountEntity> getGuestLogin(GuestEntity guestentity);
}
