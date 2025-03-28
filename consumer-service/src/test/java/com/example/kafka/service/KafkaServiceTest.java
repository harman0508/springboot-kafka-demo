package com.example.kafka.service;

import com.example.kafka.model.AuthRequest;
import com.example.kafka.model.AuthResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaServiceTest {

    private final KafkaService kafkaService = new KafkaService();

    @Test
    void testAuthorize_Accepted() {
        AuthRequest request = createAuthRequest("station-123", "id1234"); // Should return Accepted
        AuthResponse response = kafkaService.authorize(request);
        assertEquals("Accepted", response.getAuthorizationStatus());
    }

    @Test
    void testAuthorize_Rejected() {
        AuthRequest request = createAuthRequest("station-456", "id5678"); // Should return Rejected
        AuthResponse response = kafkaService.authorize(request);
        assertEquals("Rejected", response.getAuthorizationStatus());
    }

    @Test
    void testAuthorize_Unknown() {
        AuthRequest request = createAuthRequest("station-789", "unknown-id"); // Not in map, should return Unknown
        AuthResponse response = kafkaService.authorize(request);
        assertEquals("Unknown", response.getAuthorizationStatus());
    }

    @Test
    void testAuthorize_Invalid_ShortId() {
        AuthRequest request = createAuthRequest("station-000", "123"); // ID too short, should return Invalid
        AuthResponse response = kafkaService.authorize(request);
        assertEquals("Invalid", response.getAuthorizationStatus());
    }

    @Test
    void testAuthorize_Invalid_LongId() {
        AuthRequest request = createAuthRequest("station-000", "a".repeat(81)); // ID too long, should return Invalid
        AuthResponse response = kafkaService.authorize(request);
        assertEquals("Invalid", response.getAuthorizationStatus());
    }

    @Test
    void testAuthorize_Invalid_EmptyStationUuid() {
        AuthRequest request = createAuthRequest("", "id1234"); // Empty station UUID, should return Invalid
        AuthResponse response = kafkaService.authorize(request);
        assertEquals("Invalid", response.getAuthorizationStatus());
    }

    @Test
    void testAuthorize_Invalid_NullStationUuid() {
        AuthRequest request = createAuthRequest(null, "id1234"); // Null station UUID, should return Invalid
        AuthResponse response = kafkaService.authorize(request);
        assertEquals("Invalid", response.getAuthorizationStatus());
    }

    @Test
    void testAuthorize_Accepted_AnotherId() {
        AuthRequest request = createAuthRequest("station-111", "id9999"); // id9999 should return Accepted
        AuthResponse response = kafkaService.authorize(request);
        assertEquals("Accepted", response.getAuthorizationStatus());
    }

    private AuthRequest createAuthRequest(String stationUuid, String driverId) {
        AuthRequest request = new AuthRequest();
        request.setStationUuid(stationUuid);

        AuthRequest.DriverIdentifier driverIdentifier = new AuthRequest.DriverIdentifier();
        driverIdentifier.setId(driverId);

        request.setDriverIdentifier(driverIdentifier);
        return request;
    }
}
