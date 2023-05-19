package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.GuestAccountEntity;
import com.shefamma.shefamma.entities.HostAccountEntity;
import com.shefamma.shefamma.entities.HostEntity;
import org.springframework.http.ResponseEntity;

public interface HostAccount {

    String saveHostSignup(HostAccountEntity hostAccountEntity);
//    ResponseEntity<HostAccountEntity> saveHostSignup(HostAccountEntity hostAccountEntity);

    String storeHostUuid();


}
