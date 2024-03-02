package com.heroku.java.util;

import com.heroku.java.model.entity.OrderEntity;
import com.heroku.java.model.entity.UserEntity;
import com.heroku.java.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    @Autowired
    private UserRepository userRepository;

    public UserEntity toUserEntity(String apiKey, String apiSecretKey) {
        UserEntity user = userRepository.findById(apiKey).orElse(null);
        List<OrderEntity> orders = new ArrayList<>();
        if (user != null) {
            orders = userRepository.findById(apiKey).get().getOrders();
        }
        return UserEntity
                .builder()
                .userApiKey(apiKey)
                .userApiSecretKey(apiSecretKey)
                .orders(orders)
                .build();
    }
}
