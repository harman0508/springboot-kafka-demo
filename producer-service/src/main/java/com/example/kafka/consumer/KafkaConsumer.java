package com.example.kafka.consumer;

import com.example.kafka.model.AuthResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaConsumer {

    private CompletableFuture<AuthResponse> futureMessage = new CompletableFuture<>();

    @Async
    @KafkaListener(topics = "response-topic", groupId = "my-group")
    public void consume(AuthResponse message) {
        System.out.println("Received Message: " + message);
        futureMessage.complete(message);
    }

    public CompletableFuture<AuthResponse> getFutureMessage() {
        futureMessage = new CompletableFuture<>();
        return futureMessage;
    }
}
