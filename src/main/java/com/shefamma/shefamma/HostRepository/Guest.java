package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.AdressSubEntity;
import com.shefamma.shefamma.entities.GuestEntity;

public interface Guest {
    
    GuestEntity saveGuest(GuestEntity guestentity);
    GuestEntity getGuest(String guestId, String nameGuest);
    AdressSubEntity getGuest(String guestId);
    GuestEntity updateGuest(String guestId, String nameGuest,String attributeName, GuestEntity guestentity);
}
