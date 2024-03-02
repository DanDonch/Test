package com.heroku.java.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
@Slf4j
@Component
public class Signature {
    public String getSignature(String secretKey, String message) {
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to find the required algorithm {}", e.getMessage());
            throw new RuntimeException();
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        try {
            sha256_HMAC.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            log.error("Invalid key: {}", e.getMessage());
            throw new RuntimeException();
        }
        byte[] hashedBytes = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : hashedBytes) {
            hexStringBuilder.append(String.format("%02x", b));
        }
        return hexStringBuilder.toString();
    }
}
