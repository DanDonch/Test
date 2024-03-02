package com.heroku.java.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthenticationHeaders {
    private String expires;
    private String apiKey;
    private String signature;
}