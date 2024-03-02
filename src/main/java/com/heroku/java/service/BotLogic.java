package com.heroku.java.service;

import com.google.gson.Gson;
import com.heroku.java.client.BitmexClient;
import com.heroku.java.model.Bot;
import com.heroku.java.model.DataList;
import com.heroku.java.model.Order;
import com.heroku.java.model.OrderType;
import com.heroku.java.model.entity.OrderEntity;
import com.heroku.java.service.impl.OrderServiceImpl;
import com.heroku.java.util.FibonacciCreator;
import com.heroku.java.util.JsonDataParser;
import com.heroku.java.util.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BotLogic {

    @Autowired
    private BitmexClient bitmexClient;

    @Autowired
    private Bot bot;

    @Autowired
    private FibonacciCreator fibonacciCreator;

    @Autowired
    private JsonDataParser jsonDataParser;

    @Autowired
    private OrderMapper mapper;

    @Autowired
    private OrderServiceImpl orderService;

    public void processMessage(String message) {
        if (message.contains("data")) {
            String action = jsonDataParser.parse(message, "action");
            if (action != null) {
                switch (action) {
                    case "insert":
                        addOrdersToBotFromJson(message);
                        break;
                    case "update":
                        OrderEntity order = mapper.toOrderEntity(message);
                        if (!"PartiallyFilled".equals(order.getOrdStatus())) {
                            bot.getAllActiveOrders().remove(order.getOrderId());
                        }
                        if ("Canceled".equals(order.getOrdStatus()) || "Filled".equals(order.getOrdStatus())) {
                            orderService.save(order);
                            sendNewOrdersAsFibonacciSequence(order.getSide());
                        }
                        break;
                }
            }
        }
    }

    public void sendFirstOrders() {
        int[] fibo = fibonacciCreator.createFibonacciSequence(bot.getLvl());
        double lastPrice = bitmexClient.getCurrentPrice();
        for (int j : fibo) {
            double quantity = j * bot.getCof();
            double price = Math.round(lastPrice - (j * bot.getStep()));
            Order order = Order.builder()
                    .orderQty(quantity)
                    .ordType(OrderType.LMT)
                    .side("Buy")
                    .symbol("XBTUSD")
                    .price(price)
                    .build();
            bitmexClient.sendOrder(order);
            lastPrice = price;
        }
    }

    public void addOrdersToBotFromJson(String body) {
        Gson gson = new Gson();
        DataList orders = gson.fromJson(body, DataList.class);
        if (orders != null) {
            for (DataList.Data data : orders.getData()) {
                Order order = Order.builder()
                        .orderID(data.getOrderID())
                        .symbol(data.getSymbol())
                        .side(data.getSide())
                        .orderQty(data.getOrderQty())
                        .price(data.getPrice())
                        .ordType(data.getOrdType())
                        .ordStatus(data.getOrdStatus())
                        .build();
                if (order.getOrderID() != null) {
                    bot.getAllActiveOrders().put(order.getOrderID(), order);
                }
            }
        }
    }
    private void sendNewOrdersAsFibonacciSequence(String side) {
        String oppositeSide = "Buy".equals(side) ? "Sell" : "Sell".equals(side) ? "Buy" : null;
        List<String> ordersToDelete = new ArrayList<>();
        for (Map.Entry<String, Order> orderEntry : bot.getAllActiveOrders().entrySet()) {
            Order order = orderEntry.getValue();
            if (order.getSide().equals(oppositeSide)) {
                ordersToDelete.add(order.getOrderID());
            }
        }
        for (String orderId : ordersToDelete) {
            bot.getAllActiveOrders().remove(orderId);
            bitmexClient.cancelOrder(orderId);
        }
        int remainingOrdersAmount = bot.getLvl() - ordersToDelete.size() - 1;
        int amountOfOrdersNeedToBeSent = bot.getLvl() - remainingOrdersAmount;
        int[] fibo = fibonacciCreator.createFibonacciSequence(amountOfOrdersNeedToBeSent);
        double lastPrice = bitmexClient.getCurrentPrice();
        for (int j : fibo) {
            double quantity = j * bot.getCof();
            double price = 0.0;
            if (side.equals("Buy")) {
                price = Math.round(lastPrice + (j * bot.getStep()));
            } else if (side.equals("Sell")) {
                price = Math.round(lastPrice - (j * bot.getStep()));
            }
            Order order = Order.builder()
                    .orderQty(quantity)
                    .ordType(OrderType.LMT)
                    .side(oppositeSide)
                    .symbol("XBTUSD")
                    .price(price)
                    .build();
            bitmexClient.sendOrder(order);
            lastPrice = price;
        }
    }
}
