package com.heroku.java.controller;

import com.heroku.java.client.BitmexClient;
import com.heroku.java.client.BitmexWebSocket;
import com.heroku.java.client.WebSocketHandler;
import com.heroku.java.model.Bot;
import com.heroku.java.model.entity.OrderEntity;
import com.heroku.java.model.entity.UserEntity;
import com.heroku.java.service.BotLogic;
import com.heroku.java.service.impl.UserServiceImpl;
import com.heroku.java.util.UserMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Controller
@Slf4j
public class HomeController {
    @Autowired
     private Bot bot;

    @Autowired
    private BotLogic botLogic;

    @Autowired
    BitmexClient bitmexClient;

    @Autowired
    private BitmexWebSocket bitmexWebSocket;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("")
    public String home(Model model, HttpSession session) {
        setupAuthAttributes(model, session);
        return "home";
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        try {
            bitmexWebSocket.webSocketSession.close();
        } catch (IOException e) {
            log.error("Cannot close session, reason: " + e.getCause().toString());
            throw new RuntimeException(e);
        }
        bitmexWebSocket.start();
        bot.fullReset();
        session.setAttribute("apiKey", null);
        return "home";
    }
    @PostMapping("start")
    public String start(Model model, HttpSession session) {
        setupAuthAttributes(model, session);
        if (!bot.hasValidApiKeys()) {
            model.addAttribute("startStatus", "authFailed");
            return "home";
        } else if (!bot.hasValidParams()) {
            model.addAttribute("startStatus", "configFailed");
            return "home";
        } else {
            model.addAttribute("startStatus", "ok");
        }
        bitmexWebSocket.subscribeToOrders();
        botLogic.sendFirstOrders();
        return "home";
    }

    @GetMapping("stop")
    public String stop(Model model, HttpSession session) {
        setupAuthAttributes(model, session);
        model.addAttribute("stopStatus", "ok");
        bitmexWebSocket.unsubscribeFromOrders();
        bot.resetParams();
        for (String orderId : bot.getAllActiveOrders().keySet()) {
            bot.getOrdersToCancel().add(orderId);
        }
        bot.cleanOrders();
        return "home";
    }

    @RequestMapping(value = {"/login", "/start", "/setup"}, method = RequestMethod.GET)
    public String handleGetRequests(Model model, HttpSession session) {
        setupAuthAttributes(model, session);
        return "home";
    }

    @PostMapping("login")
    public String login(@RequestParam String apiKey, @RequestParam String apiSecretKey, Model model, HttpSession session) throws InterruptedException {
        setupAuthAttributes(model, session);
        model.addAttribute("bot", bot);
        bitmexWebSocket.authenticate(apiKey, apiSecretKey);
        Thread.sleep(700);
         if (WebSocketHandler.lastMessageReceived.contains("status\":401,\"error")) {
            try {
                bitmexWebSocket.webSocketSession.close();
            } catch (IOException e) {
                log.error("Cannot close session, reason: " + e.getCause().toString());
                throw new RuntimeException(e);
            }
            bitmexWebSocket.start();
            //login(apiKey, apiSecretKey, model, session);
            model.addAttribute("authStatus", "failed");
            return "home";
        } else if (WebSocketHandler.lastMessageReceived.contains("status\":400,\"error\":\"This connection is already authenticated")) {
            model.addAttribute("authStatus", "already");
        } else {
            bot.setApiKey(apiKey);
            bot.setApiSecretKey(apiSecretKey);
            model.addAttribute("authStatus", "ok");
            model.addAttribute("apiKey", apiKey);
            session.setAttribute("apiKey", apiKey);
            UserEntity user = userMapper.toUserEntity(apiKey, apiSecretKey);
            userService.save(user);
        }
        return "home";
    }

    @PostMapping("setup")
    public String setParameters(@RequestParam("step") String step, @RequestParam("coefficient") String coefficient,
            @RequestParam("level") String level, Model model, HttpSession session) {
        setupAuthAttributes(model, session);
        if (!step.matches("\\d+") || !coefficient.matches("\\d+") || !level.matches("\\d+")) {
            model.addAttribute("configStatus", "notDigits");
            return "home";
        }
        int stepValue = Integer.parseInt(step);
        int coefficientValue = Integer.parseInt(coefficient);
        int levelValue = Integer.parseInt(level);

        if (stepValue < 1 || stepValue > 100) {
            model.addAttribute("configStatus", "stepFailed");
            return "home";
        }

        if (coefficientValue < 100 || coefficientValue > 1000) {
            model.addAttribute("configStatus", "coefficientFailed");
            return "home";
        }

        if (levelValue < 1 || levelValue > 10) {
            model.addAttribute("configStatus", "levelFailed");
            return "home";
        }

        bot.setStep(stepValue);
        bot.setCof(coefficientValue);
        bot.setLvl(levelValue);
        model.addAttribute("configStatus", "ok");
        return "home";
    }

    @GetMapping("orders")
    public String viewOrders(Model model) {
        if (!bot.hasValidApiKeys()) {
            model.addAttribute("errorMessage", "You are not authorized");
            model.addAttribute("goBackButton", true);
            return "orders";
        }

        List<OrderEntity> orderList = userService.findById(bot.getApiKey()).get().getOrders();

        if (orderList.isEmpty()) {
            model.addAttribute("noOrdersMessage", "No orders available.");
        } else {
            model.addAttribute("orderList", orderList);
        }
        return "orders";
    }
    @GetMapping("cancel")
    public String cancel(Model model, HttpSession session) {
        setupAuthAttributes(model, session);
        model.addAttribute("cancelStatus", "ok");
        bitmexWebSocket.subscribeToOrders();
        for (String orderId : bot.getOrdersToCancel()) {
            bitmexClient.cancelOrder(orderId);
        }
        try {
            bitmexWebSocket.unsubscribeFromOrders();
        } finally {
            return "home";
        }
    }

    private void setupAuthAttributes(Model model, HttpSession session) {
        String apiKey = (String) session.getAttribute("apiKey");
        model.addAttribute("apiKey", apiKey);
    }
}
