package com.example.kafka.consumer;

import com.example.kafka.model.AuthRequest;
import com.example.kafka.model.AuthResponse;
import com.example.kafka.service.KafkaService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaConsumer {

    private final KafkaTemplate<String, AuthResponse> kafkaTemplate;
    private final AdminClient adminClient;
    private final KafkaService kafkaService;

    public KafkaConsumer(KafkaTemplate<String, AuthResponse> kafkaTemplate, AdminClient adminClient, KafkaService kafkaService) {
        this.kafkaTemplate = kafkaTemplate;
        this.adminClient = adminClient;
        this.kafkaService = kafkaService;
    }

    @Async
    @KafkaListener(topics = "authorization-topic", groupId = "my-group")
    public void consume(AuthRequest message) {
        System.out.println("Received Message: " + message);
        try {
            AuthResponse response = kafkaService.authorize(message);
            sendResponseMessage(response);
            System.out.println("Message send");
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in consume method" + e);
        }
    }

    public boolean topicExists(String topicName) {
        try {
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> topicNames = topics.names().get();
            return topicNames.contains(topicName);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error checking topic existence: " + e.getMessage());
            return false;
        }
    }

    public void createTopicIfNotExists(String topicName, int partitions, short replicationFactor) {
        if (!topicExists(topicName)) {
            try {
                NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
                adminClient.createTopics(Collections.singleton(newTopic)).all().get();
                System.out.println("Topic created: " + topicName);
            } catch (ExecutionException | InterruptedException e) {
                System.err.println("Error creating topic: " + e.getMessage());
            }
        } else {
            System.out.println("Topic already exists: " + topicName);
        }
    }

    public void sendResponseMessage(AuthResponse message) {
        String topic = "response-topic";
        createTopicIfNotExists(topic, 3, (short) 1);
        CompletableFuture<Void> future = kafkaTemplate.send(topic, message)
                .thenAccept(result ->
                        System.out.println("Message sent: " + message + " to topic: " + topic)
                ).exceptionally(ex -> {
                    System.err.println("Failed to send message: " + ex.getMessage());
                    return null;
                });
    }
}
