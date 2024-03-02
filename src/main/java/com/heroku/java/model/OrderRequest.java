package com.heroku.java.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderRequest {
    private final String symbol;
    private final String side;
    private final Double orderQty;
    private final Double price;
    private final String ordType;

    public static OrderRequest toRequest(Order order) {
        String symbol = order.getSymbol();
        String side = order.getSide();
        Double orderQty = order.getOrderQty();
        Double price = order.getPrice();
        String ordType = getType(order.getOrdType());
        return new OrderRequest(symbol, side, orderQty, price, ordType);
    }

    private static String getType(OrderType orderType) {
        switch (orderType) {
            case LMT: return "Limit";
            case MKT: return "Market";
            case STP_LMT: return "StopLimit";
            case STP_MKT: return "StopMarket";
            default: throw new IllegalStateException("Unsupported order type");
        }
    }
}
