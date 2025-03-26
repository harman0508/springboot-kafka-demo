package com.example.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CompletableFuture<JsonNode> futureMessage = new CompletableFuture<>();

    @KafkaListener(topics = "authorization-topic", groupId = "my-group")
    public void consume(String message) {
        System.out.println("Received Message: " + message);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Message Converted: "+jsonNode);
        futureMessage.complete(jsonNode);
    }

    public CompletableFuture<JsonNode> getFutureMessage() {
        futureMessage = new CompletableFuture<>();
        return futureMessage;
    }
}
