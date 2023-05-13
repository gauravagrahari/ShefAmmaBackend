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

    List<HostEntity> getHostsItemSearchFilter(String itemValue);

    List<HostEntity> getHostsCategorySearchFilter(String categoryValue);

    List<HostEntity> getHostsTimeSlotSearchFilter(int startTime, int endTime, String timeDuration);

//    HostEntity updateHostAttribute(String attributName, HostEntity host);
}
