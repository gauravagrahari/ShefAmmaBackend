package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.AccountEntity;
import org.springframework.http.ResponseEntity;

public interface Account {

    AccountEntity saveSignup(AccountEntity hostAccountEntity,String user);
//    ResponseEntity<HostAccountEntity> saveHostSignup(HostAccountEntity hostAccountEntity);
    String storeHostUuid();

    String storeGuestUuid();
    AccountEntity findUserByEmail(String email);

    ResponseEntity<?> changePassword(String uuidHost, String timeStamp,String newPassword);

    boolean isPasswordCorrect(String uuidHost, String oldPassword);

    String storeHostTimestamp();
//    AccountEntity findUserByEmail(String email,String phone);
}

