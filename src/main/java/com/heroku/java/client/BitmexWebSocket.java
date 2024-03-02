package com.heroku.java.client;

import com.heroku.java.service.Signature;
import com.heroku.java.util.Endpoints;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


@Component
@Slf4j
public class BitmexWebSocket {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private Signature signature;
    public WebSocketSession webSocketSession;
    private final Timer pingTimer = new Timer();

    public void start() {
        webSocketSession = connectWebSocket();
        setupPingTimer(webSocketSession);
    }

    private WebSocketSession connectWebSocket() {
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
        try {
            WebSocketSession session = webSocketClient.execute(webSocketHandler, Endpoints.BASE_TEST_URL_WEBSOCKET).get();
            session.setTextMessageSizeLimit(1024 * 1024);
            return session;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Cannot open session, reason: " + e.getCause().toString());
            throw new RuntimeException(e);
        }
    }

    public void subscribeToOrders() {
        try {
            webSocketSession.sendMessage(new TextMessage("{\"op\": \"subscribe\", \"args\": \"order\"}"));
        } catch (IOException e) {
            log.error("Cannot subscribe to orders, reason: " + e.getCause().toString());
            throw new RuntimeException(e);
        }
    }
    public void unsubscribeFromOrders() {
        try {
            webSocketSession.sendMessage(new TextMessage("{\"op\": \"unsubscribe\", \"args\": \"order\"}"));
        } catch (IOException e) {
            log.error("Cannot unsubscribe from orders, reason: " + e.getCause().toString());
            throw new RuntimeException(e);
        }
    }

    public void authenticate(String apiKey, String apiSecretKey) {
        String expires = String.valueOf(Instant.now().getEpochSecond() + 5);
        String signatureStr = signature.getSignature(apiSecretKey, "GET/realtime" + expires);
        if (!webSocketSession.isOpen()) {
            start();
        }
        try {
            webSocketSession.sendMessage(new TextMessage("{\"op\": \"authKeyExpires\", \"args\": [\"" + apiKey + "\", "
                    + expires + ", \"" + signatureStr + "\"]}"));
        } catch (IOException e) {
            log.error("Cannot send authenticate message, reason: " + e.getCause().toString());
            throw new RuntimeException(e);
        }
    }

    private void setupPingTimer(WebSocketSession webSocketSession) {
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @SneakyThrows
            public void run() {
                if(webSocketSession.isOpen()){
                    webSocketSession.sendMessage(new TextMessage("ping"));
                }
            }
        }, 5000, 5000);
    }

}
