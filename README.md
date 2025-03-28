
# Kafka Microservices with Spring Boot and Docker

This project contains two Spring Boot microservices:
- **Producer Service** (`producer-service`) - sends messages to Kafka
- **Consumer Service** (`consumer-service`) - listens for messages from Kafka

Everything is containerized and launched with Docker Compose.

## 🧱 Project Structure

```
.
├── producer-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── consumer-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
└── docker-compose.yml
```

## 🚀 How to Run the Project

Make sure Docker and Docker Compose are installed.

### 1. Build and Start All Services

```bash
docker-compose up --build
```

This will:
- Start Zookeeper and Kafka
- Build and run the producer and consumer services

### 2. Access the Services

- Producer Service: http://localhost:8081
- Consumer Service: http://localhost:8082

### 3. Send a Test Message (Optional)

You can use `curl` or Postman to send a message to the producer:

```bash
curl --location 'http://localhost:8081/api/authenticate' \
--header 'Content-Type: application/json' \
--data '{
   "stationUuid": "25aac66b-6051-478a-95e2-6d3aa343b025",
   "driverIdentifier": { "id": "id1" }
}
'
```

The consumer should log the received message in its console.

## 🛑 Stopping the Services

```bash
docker-compose down
```

This stops and removes all containers.

---

✅ Fully Dockerized Kafka with Spring Boot Microservices!
