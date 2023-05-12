package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.GuestEntity;
import com.shefamma.shefamma.entities.HostEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Host {
    HostEntity saveHost(HostEntity host);
    HostEntity getHost(String hostId,String sort);
    HostEntity getHosts(String hostId,String sort);
    HostEntity update(String hostId,String sort, HostEntity hostentity);

    List<HostEntity> getHostsItemSearchFilter(String itemValue, HostEntity hostentity);

    List<HostEntity> getHostsCategorySearchFilter(String categoryValue, HostEntity hostentity);

    List<HostEntity> getHostsTimeSlotSearchFilter(String startTime, String endTime, HostEntity hostentity);

//    HostEntity updateHostAttribute(String attributName, HostEntity host);
}
