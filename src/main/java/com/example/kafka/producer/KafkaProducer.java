package com.example.kafka.producer;

import com.example.kafka.model.AuthRequest;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String, AuthRequest> kafkaTemplate;
    private final AdminClient adminClient;

    public KafkaProducer(KafkaTemplate<String, AuthRequest> kafkaTemplate, AdminClient adminClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.adminClient = adminClient;
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

    public void sendMessage(AuthRequest message) {
        String topic = "authorization-topic";
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
