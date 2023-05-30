package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.GuestAccountEntity;
import com.shefamma.shefamma.entities.HostAccountEntity;

public interface HostAccount {

    String saveHostSignup(HostAccountEntity hostAccountEntity);
//    ResponseEntity<HostAccountEntity> saveHostSignup(HostAccountEntity hostAccountEntity);

    String storeHostUuid();


}
