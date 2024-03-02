package com.heroku.java.service.impl;

import com.heroku.java.model.entity.UserEntity;
import com.heroku.java.repository.UserRepository;
import com.heroku.java.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> findById(String apiKey) {
        return userRepository.findById(apiKey);
    }
}
