package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.GuestAccountEntity;
import com.shefamma.shefamma.entities.GuestEntity;
import org.springframework.http.ResponseEntity;

public interface GuestAccount {
    String saveGuestSignup(GuestAccountEntity guestEntity);
    String storeHostUuid();
}
