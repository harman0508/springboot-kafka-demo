package com.example.kafka.controller;

import com.example.kafka.consumer.KafkaConsumer;
import com.example.kafka.model.AuthRequest;
import com.example.kafka.model.AuthResponse;
import com.example.kafka.producer.KafkaProducer;
import com.example.kafka.service.KafkaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class KafkaController {

    private final KafkaProducer kafkaProducer;
    private final KafkaConsumer kafkaConsumer;
    private final ObjectMapper objectMapper;
    private KafkaService kafkaService;

    public KafkaController(KafkaProducer kafkaProducer, KafkaConsumer kafkaConsumer, ObjectMapper objectMapper, KafkaService kafkaService) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
        this.objectMapper = objectMapper;
        this.kafkaService = kafkaService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        kafkaProducer.sendMessage(request);
        try {
            JsonNode jsonNode = kafkaConsumer.getFutureMessage().get(10, TimeUnit.SECONDS);
            AuthResponse response = kafkaService.authorize(objectMapper.treeToValue(jsonNode, AuthRequest.class));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new AuthResponse("Error: " + e.getMessage()));
        }
    }
}
