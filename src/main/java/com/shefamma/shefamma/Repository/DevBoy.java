package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.DevBoyEntity;

public interface DevBoy {

    DevBoyEntity update(String uuidDevBoy, String geocode, String attributeName, DevBoyEntity hostentity);

    DevBoyEntity saveDevBoy(DevBoyEntity devBoyEntity);
}
