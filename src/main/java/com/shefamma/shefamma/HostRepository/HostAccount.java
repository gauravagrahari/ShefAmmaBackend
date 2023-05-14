package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.HostAccountEntity;
import com.shefamma.shefamma.entities.HostEntity;

public interface HostAccount {

    HostAccountEntity saveHostSignup(HostAccountEntity hostentity);

    HostEntity getHostLogin(HostEntity hostentity);


}
