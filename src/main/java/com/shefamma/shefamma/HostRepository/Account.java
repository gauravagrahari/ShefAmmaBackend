package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.AccountEntity;

public interface Account {

    String saveSignup(AccountEntity hostAccountEntity,String user);
//    ResponseEntity<HostAccountEntity> saveHostSignup(HostAccountEntity hostAccountEntity);
    String storeHostUuid();

    String storeGuestUuid();
    AccountEntity findUserByEmail(String email);
//    AccountEntity findUserByEmail(String email,String phone);
}

