package com.heroku.java.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Data
@Component
public class Bot {
    private String apiKey;
    private String apiSecretKey;
    private double step;
    private double cof;
    private int lvl;
    private HashMap<String, Order> allActiveOrders = new HashMap<>();
    private List<String> ordersToCancel = new ArrayList<>();

    public boolean hasValidApiKeys() {
        return apiKey != null && apiSecretKey != null;
    }
    public boolean hasValidParams() {
        return step != 0. && cof != 0. && lvl != 0;
    }
    public void fullReset() {
        setApiKey(null);
        setApiSecretKey(null);
        setStep(0);
        setCof(0);
        setLvl(0);
        allActiveOrders = new HashMap<>();
        ordersToCancel = new ArrayList<>();
    }
    public void resetParams() {
        setStep(0);
        setCof(0);
        setLvl(0);
    }
    public void cleanOrders() {
        allActiveOrders = new HashMap<>();
    }
}
