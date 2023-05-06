package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.ItemEntity;

import java.util.List;

public interface Item {
    ItemEntity saveItem(ItemEntity itementity);

    ItemEntity updateItem(String hostId, String nameItem, ItemEntity itementity);

    List<ItemEntity> getItems(String hostId, ItemEntity itementity);

    ItemEntity getItem(String hostId, String nameItem, ItemEntity itementity);
}
