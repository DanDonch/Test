package com.heroku.java.service;

import com.heroku.java.model.entity.UserEntity;

import java.util.Optional;

public interface UserService {
    UserEntity save(UserEntity userEntity);
    Optional<UserEntity> findById(String apiKey);
}
