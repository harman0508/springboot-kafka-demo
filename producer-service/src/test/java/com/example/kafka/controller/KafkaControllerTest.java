package com.example.kafka.controller;

import com.example.kafka.consumer.KafkaConsumer;
import com.example.kafka.model.AuthRequest;
import com.example.kafka.model.AuthResponse;
import com.example.kafka.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaControllerTest {

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private KafkaConsumer kafkaConsumer;

    @InjectMocks
    private KafkaController kafkaController;

    private AuthRequest mockAuthRequest;
    private AuthResponse mockAuthResponse;

    @BeforeEach
    void setUp() {
        mockAuthRequest = new AuthRequest();
        mockAuthRequest.setStationUuid("station-123");
        AuthRequest.DriverIdentifier driverIdentifier = new AuthRequest.DriverIdentifier();
        driverIdentifier.setId("id1234");
        mockAuthRequest.setDriverIdentifier(driverIdentifier);

        mockAuthResponse = new AuthResponse("Accepted");
    }

    @Test
    void testAuthenticate_successful() throws Exception {

        CompletableFuture<AuthResponse> future = new CompletableFuture<>();
        future.complete(mockAuthResponse);

        when(kafkaConsumer.getFutureMessage()).thenReturn(future);

        ResponseEntity<AuthResponse> response = kafkaController.authenticate(mockAuthRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Accepted", Objects.requireNonNull(response.getBody()).getAuthorizationStatus());
        verify(kafkaProducer, times(1)).sendMessage(mockAuthRequest);
    }

    @Test
    void testAuthenticate_timeoutError() throws Exception {
        CompletableFuture<AuthResponse> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Timeout"));

        when(kafkaConsumer.getFutureMessage()).thenReturn(future);

        ResponseEntity<AuthResponse> response = kafkaController.authenticate(mockAuthRequest);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(Objects.requireNonNull(response.getBody()).getAuthorizationStatus().contains("Error"));
        verify(kafkaProducer, times(1)).sendMessage(mockAuthRequest);
    }
}
