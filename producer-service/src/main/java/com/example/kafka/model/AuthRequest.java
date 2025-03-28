package com.example.kafka.model;

public class AuthRequest {
    private String stationUuid;
    private DriverIdentifier driverIdentifier;

    public String getStationUuid() {
        return stationUuid;
    }

    public void setStationUuid(String stationUuid) {
        this.stationUuid = stationUuid;
    }

    public DriverIdentifier getDriverIdentifier() {
        return driverIdentifier;
    }

    public void setDriverIdentifier(DriverIdentifier driverIdentifier) {
        this.driverIdentifier = driverIdentifier;
    }

    public static class DriverIdentifier {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
