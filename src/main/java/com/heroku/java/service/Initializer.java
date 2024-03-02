package com.heroku.java.service;

import com.heroku.java.client.BitmexWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements ApplicationRunner {
    @Autowired
    private BitmexWebSocket bitmexWebSocket;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        bitmexWebSocket.start();
    }
}


