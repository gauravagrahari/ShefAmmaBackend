package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.ItemEntity;

import java.util.List;

public interface Item {
    ItemEntity saveItem(ItemEntity itementity);

    ItemEntity updateItem(String hostId, String nameItem, ItemEntity itementity);

    List<ItemEntity> getItems(String itemId);

    ItemEntity getItem(String hostId, String nameItem, ItemEntity itementity);

    ItemEntity updateItemAttribute(String partition, String sort, String attributeName, ItemEntity itemEntity);
}
