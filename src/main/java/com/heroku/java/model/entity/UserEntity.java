package com.heroku.java.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserEntity {
    @Id
    @Column(name = "user_api_key", nullable = false, unique = true)
    private String userApiKey;

    @Column(name = "user_api_secret_key", nullable = false)
    private String userApiSecretKey;

    @OneToMany(mappedBy = "user")
    private List<OrderEntity> orders;

}
