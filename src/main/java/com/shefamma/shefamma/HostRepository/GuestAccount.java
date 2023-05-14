package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.GuestAccountEntity;
import com.shefamma.shefamma.entities.GuestEntity;

public interface GuestAccount {
    GuestAccountEntity saveGuestSignup(GuestAccountEntity guestEntity);
    GuestEntity getGuestLogin(GuestEntity guestentity);
}
