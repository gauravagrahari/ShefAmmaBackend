package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.Repository.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;

//@Controller
public class HostSignUp {
    private Account hostAccount;
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
