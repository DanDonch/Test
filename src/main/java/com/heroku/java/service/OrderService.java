package com.heroku.java.service;

import com.heroku.java.model.entity.OrderEntity;

public interface OrderService {
    OrderEntity save(OrderEntity orderEntity);
}
