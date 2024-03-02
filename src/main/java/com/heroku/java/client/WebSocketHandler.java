package com.heroku.java.client;

import com.heroku.java.service.BotLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Component
@Slf4j
public class WebSocketHandler extends AbstractWebSocketHandler {

    @Autowired
    private BotLogic botLogic;
    public static String lastMessageReceived;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String serverMessage = message.getPayload();
        if (isMessageProcessingNeeded(serverMessage)) {
            log.info(serverMessage);
            lastMessageReceived = serverMessage;
            botLogic.processMessage(serverMessage);
        }
    }

    private boolean isMessageProcessingNeeded(String message) {
       return  !message.contains("Canceled via API")
               && !message.contains("partial")
               && !message.contains("pong")
               && !message.contains("Welcome");

   }
}
