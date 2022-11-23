package me.zhulin.shopapi.service;

import me.zhulin.shopapi.entity.ProductInOrder;
import me.zhulin.shopapi.entity.User;


public interface ProductInOrderService {
    void update(String itemId, Integer quantity, User user);
    ProductInOrder findOne(String itemId, User user);
}
