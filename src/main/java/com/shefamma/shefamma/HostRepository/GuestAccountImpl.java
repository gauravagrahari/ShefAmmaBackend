package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.GuestAccountEntity;
import com.shefamma.shefamma.entities.GuestEntity;
import org.springframework.stereotype.Repository;

@Repository
public class GuestAccountImpl implements GuestAccount{
    @Override
    public GuestAccountEntity saveGuestSignup(GuestAccountEntity guestEntity) {
        return null;
    }

    @Override
    public GuestEntity getGuestLogin(GuestEntity guestentity) {
        return null;
    }
}
