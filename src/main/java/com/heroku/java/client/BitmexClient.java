package com.heroku.java.client;

import com.google.gson.Gson;
import com.heroku.java.exception.GlobalExceptionHandler;
import com.heroku.java.model.AuthenticationHeaders;
import com.heroku.java.model.Order;
import com.heroku.java.model.OrderRequest;
import com.heroku.java.service.AuthenticationService;
import com.heroku.java.service.OrderHttpRequest;
import com.heroku.java.util.Endpoints;
import com.heroku.java.util.JsonDataParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Slf4j
public class BitmexClient {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JsonDataParser jsonDataParser;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Gson gson = new Gson();

    public void sendOrder(Order order) {
        String httpMethod = "POST";
        String data;
        String base = "/api/v1";
        if (order == null) {
            data = "";
        } else {
            OrderRequest orderRequest = OrderRequest.toRequest(order);
            data = gson.toJson(orderRequest);
        }
        OrderHttpRequest orderHttpRequest = new OrderHttpRequest(order, Endpoints.BASE_TEST_URL, Endpoints.ORDER_ENDPOINT, httpMethod,
                authenticationService.getAuthenticationHeaders(httpMethod, data, base+Endpoints.ORDER_ENDPOINT));
        try {
            httpClient.send(orderHttpRequest.getHttpRequest(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Cannot sent http request, reason: " + e.getCause().toString());
            throw new RuntimeException();
        }
    }

    public void cancelOrder(String orderId) {
        String httpMethod = "DELETE";
        String data = "{\"orderID\": \"" + orderId + "\"}";
        String base = "/api/v1";
        AuthenticationHeaders authenticationHeaders = authenticationService.getAuthenticationHeaders(httpMethod, data,
                base + Endpoints.ORDER_ENDPOINT);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(Endpoints.BASE_TEST_URL + Endpoints.ORDER_ENDPOINT))
                .method(httpMethod, HttpRequest.BodyPublishers.ofString(data))
                .header("api-key", authenticationHeaders.getApiKey())
                .header("api-signature", authenticationHeaders.getSignature())
                .header("api-expires", authenticationHeaders.getExpires())
                .header("Content-Type", "application/json")
                .build();
        try {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Cannot sent http request, reason: " + e.getCause().toString());
            throw new RuntimeException();
        }
    }

    public double getCurrentPrice() {
        String httpMethod = "GET";
        String data = "";
        String base = "/api/v1";
        OrderHttpRequest orderHttpRequest = new OrderHttpRequest(null, Endpoints.BASE_TEST_URL,
                Endpoints.INSTRUMENT_ENDPOINT+Endpoints.XBTUSD_SYMBOL_PARAMETER, httpMethod,
                authenticationService.getAuthenticationHeaders(httpMethod, data,
                        base+ Endpoints.INSTRUMENT_ENDPOINT+Endpoints.XBTUSD_SYMBOL_PARAMETER));
        try {
            HttpResponse<String> response = httpClient.send(orderHttpRequest.getHttpRequest(), HttpResponse.BodyHandlers.ofString());
            return jsonDataParser.parsePrice(response.body());
        } catch (IOException | InterruptedException e) {
            log.error("Cannot sent http request, reason: " + e.getCause().toString());
            throw new RuntimeException();
        }
    }
}
