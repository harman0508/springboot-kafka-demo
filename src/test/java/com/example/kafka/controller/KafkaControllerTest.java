package com.example.kafka.controller;

import com.example.kafka.consumer.KafkaConsumer;
import com.example.kafka.model.AuthRequest;
import com.example.kafka.model.AuthResponse;
import com.example.kafka.producer.KafkaProducer;
import com.example.kafka.service.KafkaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaControllerTest {

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private KafkaConsumer kafkaConsumer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaService kafkaService;

    @InjectMocks
    private KafkaController kafkaController;

    private AuthRequest mockAuthRequest;
    private AuthResponse mockAuthResponse;
    private JsonNode mockJsonNode;

    @BeforeEach
    void setUp() {
        mockAuthRequest = new AuthRequest();
        mockAuthRequest.setStationUuid("station-123");
        AuthRequest.DriverIdentifier driverIdentifier = new AuthRequest.DriverIdentifier();
        driverIdentifier.setId("id1234");
        mockAuthRequest.setDriverIdentifier(driverIdentifier);

        mockAuthResponse = new AuthResponse("Accepted");

        mockJsonNode = mock(JsonNode.class); // Mock JsonNode
    }

    @Test
    void testAuthenticate_Accepted() throws Exception {
        // Mock KafkaProducer behavior
        doNothing().when(kafkaProducer).sendMessage(any(AuthRequest.class));

        // Mock KafkaConsumer behavior (returning JSON Node)
        CompletableFuture<JsonNode> future = new CompletableFuture<>();
        future.complete(mockJsonNode);
        when(kafkaConsumer.getFutureMessage()).thenReturn(future);

        // Mock ObjectMapper conversion
        when(objectMapper.treeToValue(mockJsonNode, AuthRequest.class)).thenReturn(mockAuthRequest);

        // Mock KafkaService behavior
        when(kafkaService.authorize(any(AuthRequest.class))).thenReturn(mockAuthResponse);

        // Call the method
        ResponseEntity<AuthResponse> responseEntity = kafkaController.authenticate(mockAuthRequest);

        // Verify response
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("Accepted", responseEntity.getBody().getAuthorizationStatus());

        // Verify KafkaProducer interaction
        verify(kafkaProducer, times(1)).sendMessage(any(AuthRequest.class));
        verify(kafkaConsumer, times(1)).getFutureMessage();
        verify(kafkaService, times(1)).authorize(any(AuthRequest.class));
    }
}
