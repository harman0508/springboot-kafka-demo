# Spring Boot Kafka Project - README

This guide explains how to run the Spring Boot Kafka project locally and using Docker.

---

## Running Locally (Without Docker)
### Prerequisites
Ensure you have installed:
- Java 17+ (Check with `java -version`)
- Apache Kafka (Download from https://kafka.apache.org/downloads)
- Zookeeper (Comes with Kafka)
- Maven (Check with `mvn -version`)

### Step 1: Start Zookeeper & Kafka
Navigate to your Kafka installation directory and start the services:
```
# Start Zookeeper
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Start Kafka
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

### This step is Optional: Step 2: Create Kafka Topic (Optional)
```
bin/kafka-topics.sh --create --topic authorization-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### Step 3: Run Spring Boot Application
```
mvn spring-boot:run
```
or
```
java -jar target/springboot-kafka-demo-0.0.1-SNAPSHOT.jar
```

---

## Running with Docker
### Prerequisites
Ensure you have installed:
- Docker (Check with `docker -v`)
- Docker Compose (Check with `docker-compose -v`)

### Step 1: Build and Start Services
Run the following command to start Zookeeper, Kafka, and the Spring Boot app using Docker:
```
docker-compose up --build
```

### Step 2: Check Running Containers
```
docker ps
```

### Step 3: Verify Kafka is Running
Check if topics exist:
```
docker exec -it kafka kafka-topics.sh --bootstrap-server kafka:9092 --list
```

### Step 4: Stop the Containers
To stop all running containers:
```
docker-compose down
```

---

## 🛠 Useful Commands
### Kafka Commands in Docker
```
# Create a topic
docker exec -it kafka kafka-topics.sh --create --topic my-topic --bootstrap-server kafka:9092 --partitions 1 --replication-factor 1

# List topics
docker exec -it kafka kafka-topics.sh --bootstrap-server kafka:9092 --list

# Describe topic
docker exec -it kafka kafka-topics.sh --describe --topic my-topic --bootstrap-server kafka:9092

# Consume messages
docker exec -it kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic my-topic --from-beginning
```

### Docker Commands
```
# Restart all containers
docker-compose restart

# View logs
docker logs -f kafka
docker logs -f my-app

# Remove all stopped containers
docker system prune -f
```

---

## Testing with Postman
### Send a Message to Kafka
- **URL:** `POST http://localhost:8080/api/authenticate`
- **Body (JSON):**
```
{
   "stationUuid": "25aac66b-6051-478a-95e2-6d3aa343b025",
   "driverIdentifier": { "id": "id124" }
}
```
---

## Troubleshooting
### Kafka Broker Not Available?
- Check if Kafka is running:
```
docker logs kafka
```
- Ensure Kafka's advertised listener is correctly set to `PLAINTEXT://kafka:9092` in `docker-compose.yml`.

### Port Conflicts?
If `9092` is already in use, update Kafka’s port in `docker-compose.yml`:
```
ports:
  - "29092:9092"
```
Then update `application.yml`:
```
spring.kafka.bootstrap-servers=kafka:29092
```

---

## Summary
| Method | Command |
|--------|---------|
| **Run Locally** | `mvn spring-boot:run` |
| **Run with Docker** | `docker-compose up --build` |
| **Check Running Containers** | `docker ps` |
| **Stop Docker Containers** | `docker-compose down` |
| **Create Kafka Topic** | `docker exec -it kafka kafka-topics.sh --create --topic my-topic --bootstrap-server kafka:9092` |
| **Send Message (Postman)** | `POST http://localhost:8080/api/authenticate` |

