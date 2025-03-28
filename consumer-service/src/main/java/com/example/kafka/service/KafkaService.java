package com.example.kafka.service;

import com.example.kafka.model.AuthRequest;
import com.example.kafka.model.AuthResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaService {

    private static final Map<String, String> IDENTIFIER_STATUS = Map.of(
            "id1234", "Accepted",
            "id5678", "Rejected",
            "id9999", "Accepted"
    );

    public AuthResponse authorize(AuthRequest request) {
        if (request.getStationUuid() == null || request.getStationUuid().trim().isEmpty()) {
            return new AuthResponse("Invalid");
        }
        String id = request.getDriverIdentifier().getId();

        if (id == null || id.length() < 5 || id.length() > 80) {
            return new AuthResponse("Invalid");
        }

        if (IDENTIFIER_STATUS.containsKey(id)) {
            return new AuthResponse(IDENTIFIER_STATUS.get(id));
        }

        return new AuthResponse("Unknown");
    }
}
