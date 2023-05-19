package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.HostRepository.HostAccount;
import com.shefamma.shefamma.entities.HostAccountEntity;
import com.shefamma.shefamma.entities.HostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//@Controller
public class HostSignUp {
    private HostAccount hostAccount;
    private UserDetailsService userDetails;
    @Autowired
    private UserDetailsService userDetailsService;
//    @PostMapping("/hostSignup")
//    public ResponseEntity<HostAccountEntity> saveHostSignup(@RequestBody HostAccountEntity hostentity) {
//        userDetailsService.loadUserByUsername(hostentity.getHostPhone());
//        return hostAccount.saveHostSignup(hostentity);
//    }


//    @PostMapping("/hostLogin")
//    public ResponseEntity<HostEntity> getHostLogin(@RequestBody HostEntity hostentity) {
//        return hostAccount.getHostLogin(hostentity);
//    }


}
