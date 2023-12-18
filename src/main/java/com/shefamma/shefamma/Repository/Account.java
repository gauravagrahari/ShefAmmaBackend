package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.AccountEntity;
import org.springframework.http.ResponseEntity;

public interface Account {

    AccountEntity saveSignup(AccountEntity hostAccountEntity,String user);

    String storeHostUuid();

    String storeGuestUuid();
    AccountEntity findUserByEmail(String email);

    ResponseEntity<?> changePassword(String uuidHost, String timeStamp,String newPassword);

    boolean isPasswordCorrect(String uuidHost, String oldPassword);

    String storeDevBoyUuid();

    String storeTimestamp();

    String storeAdminUuid();

}

