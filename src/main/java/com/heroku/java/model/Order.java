package com.heroku.java.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {
    private String orderID;
    private String symbol;
    private String side;
    private double orderQty;
    private Double price;
    private OrderType ordType;
    private String ordStatus;
}