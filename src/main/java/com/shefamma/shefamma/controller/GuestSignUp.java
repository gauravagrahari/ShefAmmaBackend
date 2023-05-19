package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.HostRepository.GuestAccount;
import com.shefamma.shefamma.entities.GuestAccountEntity;
import com.shefamma.shefamma.entities.GuestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//@Controller
public class GuestSignUp {
    public GuestAccount guestAccount;
    @PostMapping("/guestSignup")
    public ResponseEntity<GuestAccountEntity> saveGuestSignup(@RequestBody GuestAccountEntity guestentity) {
        return guestAccount.saveGuestSignup(guestentity);
    }

    @PostMapping("/guestLogin")
    public ResponseEntity<GuestAccountEntity> getGuestLogin(@RequestBody GuestEntity guestentity) {
        return guestAccount.getGuestLogin(guestentity);
    }
}
