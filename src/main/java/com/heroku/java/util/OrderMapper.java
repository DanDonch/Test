package com.heroku.java.util;

import com.heroku.java.model.Bot;
import com.heroku.java.model.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class OrderMapper {

    @Autowired
    private JsonDataParser jsonDataParser;

    @Autowired
    private Bot bot;

    @Autowired
    private UserMapper userMapper;

    private final DateTimeFormatter sourceFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private final DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderEntity toOrderEntity(String message) {
        String orderID = jsonDataParser.parseData(message, "orderID");
        String ordStatus = jsonDataParser.parseData(message, "ordStatus");
        String side = jsonDataParser.parseData(message, "side");
        Double orderQty = Double.valueOf(jsonDataParser.parseData(message, "orderQty"));
        Double price = Double.valueOf(jsonDataParser.parseData(message, "price"));
        LocalDateTime timestamp = LocalDateTime.parse(jsonDataParser.parseData(message, "timestamp"), sourceFormatter);
        String timestampAsString = timestamp.format(targetFormatter);
        return OrderEntity
                .builder()
                .orderId(orderID)
                .side(side)
                .orderQty(orderQty)
                .price(price)
                .ordStatus(ordStatus)
                .timestamp(timestampAsString)
                .user(userMapper.toUserEntity(bot.getApiKey(), bot.getApiSecretKey()))
                .build();
    }
}
