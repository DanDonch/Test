package com.heroku.java.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderEntity {
    @Id
    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private String side;

    @Column(name = "order_qty", nullable = false)
    private Double orderQty;

    @Column(nullable = false)
    private Double price;

    @Column(name = "ord_status", nullable = false)
    private String ordStatus;

    @Column(nullable = false)
    private String timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_api_key")
    private UserEntity user;
}
