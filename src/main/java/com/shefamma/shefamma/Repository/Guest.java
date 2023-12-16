package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.AdressSubEntity;
import com.shefamma.shefamma.entities.GuestEntity;

public interface Guest {
    
    GuestEntity saveGuest(GuestEntity guestentity);
    GuestEntity getGuestAddress(String guestId, String nameGuest);
    AdressSubEntity getGuestAddress(String guestId);
    GuestEntity updateGuest(String guestId, String nameGuest,String attributeName, GuestEntity guestentity);

    GuestEntity getGuestUsingPk(String uuidGuest);
}
