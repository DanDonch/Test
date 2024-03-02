package com.heroku.java.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

@Component
public class JsonDataParser {

    public double parsePrice(String body) {
        JsonArray jsonArray = JsonParser.parseString(body).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        return jsonObject.get("lastPrice").getAsDouble();
    }

    public String parse(String body, String key) {
        JsonElement jsonElement = JsonParser.parseString(body);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement keyElement = jsonObject.get(key);
            if (keyElement != null && keyElement.isJsonPrimitive()) {
                return keyElement.getAsJsonPrimitive().getAsString();
            }
        }
        return null;
    }

    public String parseData(String body, String key) {
        JsonElement jsonElement = JsonParser.parseString(body);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
                JsonArray dataArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : dataArray) {
                    if (element.isJsonObject()) {
                        JsonObject dataObject = element.getAsJsonObject();
                        if (dataObject.has(key) && dataObject.get(key).isJsonPrimitive()) {
                            return dataObject.getAsJsonPrimitive(key).getAsString();
                        }
                    }
                }
            }
        }
        return null;
    }
}
