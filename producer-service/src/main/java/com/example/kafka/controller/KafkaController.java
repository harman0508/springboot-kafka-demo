package com.example.kafka.controller;

import com.example.kafka.consumer.KafkaConsumer;
import com.example.kafka.model.AuthRequest;
import com.example.kafka.model.AuthResponse;
import com.example.kafka.producer.KafkaProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class KafkaController {

    private final KafkaProducer kafkaProducer;
    private final KafkaConsumer kafkaConsumer;

    public KafkaController(KafkaProducer kafkaProducer, KafkaConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        kafkaProducer.sendMessage(request);
        try {
            AuthResponse jsonNode = kafkaConsumer.getFutureMessage().get(10, TimeUnit.SECONDS);
            return ResponseEntity.ok(jsonNode);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new AuthResponse("Error: " + e.getMessage()));
        }
    }
}
