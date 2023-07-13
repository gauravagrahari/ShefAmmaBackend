package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.HostCardEntity;
import com.shefamma.shefamma.entities.HostEntity;
import com.shefamma.shefamma.entities.OrderEntity;

import java.util.List;

public interface Host {
    HostEntity saveHost(HostEntity host);
    HostEntity getHost(String hostId,String sort);
    HostEntity getHosts(String hostId,String sort);
    HostEntity update(String partition, String sort, String attributeName, HostEntity hostentity);
    List<HostCardEntity> getHostsItemSearchFilter(double latitude, double longitude, double radius, String itemValue);

    List<HostEntity> getHostsCategorySearchFilter(String categoryValue);

    List<HostEntity> getHostsCategorySearchFilter(double latitude, double longitude, double radius, String dineCategoryValue);

    List<HostEntity> getHostsTimeSlotSearchFilter(int startTime, int endTime, String timeDuration);

    List<HostCardEntity> findRestaurantsWithinRadius(double latitude, double longitude, double radius);

    OrderEntity getHostRatingReview(HostEntity hostEntity);

    HostEntity updateHostRating(String hostId, double userRating);

//    HostEntity updateHostAttribute(String attributName, HostEntity host);
}
