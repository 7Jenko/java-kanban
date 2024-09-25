package com.yandex.app.http;

import com.google.gson.GsonBuilder;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.yandex.app.utils.DurationAdapter;
import com.yandex.app.utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetHistory(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getHistory();
        String response;
        if (history.isEmpty()) {
            response = "История пуста!";
        } else {
            response = gson.toJson(history);
        }
        sendText(exchange, response, 200);
    }
}