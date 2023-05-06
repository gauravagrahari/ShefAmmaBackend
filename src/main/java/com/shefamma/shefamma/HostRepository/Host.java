package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.GuestEntity;
import com.shefamma.shefamma.entities.HostEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Host {
    HostEntity saveHost(HostEntity host);
    HostEntity getHost(String hostId,String sort);
    HostEntity update(String hostId,String sort, HostEntity hostentity);

//    HostEntity updateHostAttribute(String attributName, HostEntity host);
}
