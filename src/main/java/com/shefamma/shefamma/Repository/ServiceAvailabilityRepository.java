package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.ServiceAvailability;

public interface ServiceAvailabilityRepository {
    ServiceAvailability getServiceMessage();

    void updateServiceMessage(String message);
}
