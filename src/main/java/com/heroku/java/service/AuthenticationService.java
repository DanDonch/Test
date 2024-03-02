package com.heroku.java.service;

import com.heroku.java.model.AuthenticationHeaders;
import com.heroku.java.model.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

    @Autowired
    private Signature signature;

    @Autowired
    private Bot bot;
    public AuthenticationHeaders getAuthenticationHeaders(String httpMethod, String data, String path) {
        long expires = System.currentTimeMillis() / 1000 + 5;
        String signatureStr = signature.getSignature(bot.getApiSecretKey(), httpMethod + path + expires + data);
        return  AuthenticationHeaders.builder()
                .apiKey(bot.getApiKey())
                .signature(signatureStr)
                .expires(Long.toString(expires))
                .build();
    }
}