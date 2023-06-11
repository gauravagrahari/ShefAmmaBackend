package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.GuestEntity;

public interface Guest {
    
    GuestEntity saveGuest(GuestEntity guestentity);

    GuestEntity getGuest(String guestId, String nameGuest);

    GuestEntity updateGuest(String guestId, String nameGuest,String attributeName, GuestEntity guestentity);
}
