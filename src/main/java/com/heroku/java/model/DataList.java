package com.heroku.java.model;

import lombok.Getter;

import java.util.List;

@Getter
public class DataList {
    private List<Data> data;

    @Override
    public String toString() {
        return "DataListDataList{" +
                "data=" + getData() +
                '}';
    }

    @Getter
    public static class Data {
        private String orderID;
        private String symbol;
        private String side;
        private double orderQty;
        private Double price;
        private OrderType ordType;
        private String ordStatus;

        @Override
        public String toString() {
            return "Data{" +
                    "orderId='" + orderID + '\'' +
                    ", symbol='" + symbol + '\'' +
                    ", side='" + side + '\'' +
                    ", orderQty=" + orderQty +
                    ", price=" + price +
                    ", ordType=" + ordType +
                    ", ordStatus='" + ordStatus + '\'' +
                    '}';
        }
    }
}


