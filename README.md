
# Quarkus WebSocket Messaging Application

## Introduction

This project is a **Quarkus-based WebSocket Messaging Application** that provides a chat platform allowing users to send real-time messages to individuals or groups. The application supports both text and media messages (images, videos) via AWS S3 integration. 
The project also implements user preferences, allowing users to control settings such as read receipts, notification muting, and visibility of seen status.

---

## Project Structure

The project follows a well-organized structure adhering to industry standards for scalability and maintainability.

```
chatservice
├── src
│   ├── main
│   │   ├── java
│   │   │   └── co
│   │   │       └── vivo
│   │   │           └── chatservice
│   │   │               ├── controller    # REST Controllers
│   │   │               ├── dto           # Data Transfer Objects (DTO)
│   │   │               ├── enums         # Enum definitions (e.g., UserType, ReadReceipt)
│   │   │               ├── model         # JPA Entity classes
│   │   │               ├── repository    # Repositories for data access
│   │   │               ├── service       # Business logic services
│   │   │               ├── util          # Utility classes
│   ├── resources
│   │   ├── application.properties        # Application configuration (DB, Redis, AWS, WebSocket, etc.)
│   └── test
```

---

## Controllers

### 1. **AuthResource**(`co.vivo.chatservice.controller.AuthResource`)
Handles user authentication, registration, and login.

- `/auth/register` - Register a new user.
- `/auth/login` - Login and receive JWT token for authentication.

### 2. **ChatSocket** (`co.vivo.chatservice.controller.ChatSocket`)
Provides enhanced WebSocket endpoints for group messaging.

- `/chat/{userId}` - Group chat messaging endpoint.

### 3. **MediaController** (`co.vivo.chatservice.controller.MediaController`)
Manages media (images/videos) uploads.

- `/media/upload` - Upload a media file to AWS S3.

### 4. **UserPreferencesController** (`co.vivo.chatservice.controller.UserPreferencesController`)
Manages user preferences for notifications and read receipts.

- `/user-preferences/{userId}` - Set and get user preferences.

### 5. **ChatController** (`co.vivo.chatservice.controller.ChatController`)
Manages user chats, contacts between individual and groups.

- `/chat/messages/{userId}` - Get Chat messages for target user.

### 6. **ChatGroupController** (`co.vivo.chatservice.controller.ChatGroupController`)
Manages user groups for creating groups and sending group messagesetc.

- `/groups/{userId}` - Get all the groups user has joined.


---

## Services

### 1. **AuthService** (`co.vivo.chatservice.service.AuthService`)
Handles user authentication, JWT generation, and verification.

### 2. **UserService** (`co.vivo.chatservice.service.UserService`)
Manages user registrations, retrieval, and updates.

### 3. **UserPreferencesService** (`co.vivo.chatservice.service.UserPreferencesService`)
Handles CRUD operations for user preferences like read receipts, notification muting, etc.

### 4. **MediaService** (`co.vivo.chatservice.service.MediaService`)
Manages media uploads and AWS S3 integration.

---

## Entities

### 1. **UserEntity** (`co.vivo.chatservice.model.UserEntity`)
Represents the user entity in the system with fields like `userId`, `email`, `mobile`, `username`, and `password`.

### 2. **UserPreferences** (`co.vivo.chatservice.model.UserPreferences`)
Stores user preferences such as `readReceipt`, `muteNotifications`, and `hideSeenStatus`.

---

## Repositories

### 1. **UserRepository** (`co.vivo.chatservice.repository.UserRepository`)
Handles data persistence and retrieval for `UserEntity`.

### 2. **UserPreferencesRepository** (`co.vivo.chatservice.repository.UserPreferencesRepository`)
Handles data persistence and retrieval for `UserPreferences`.

---

## Building and Deploying the Project

### **Step 1: Clone the Repository**
```bash
git clone https://github.com/asadali08527/instant-messaging-service-via-websocket.git
cd instant-messaging-service-via-websocket
```

### **Step 2: Build the Project**
Make sure you have Maven and JDK 17 installed.

```bash
mvn clean package -DskipTests
```

This will package the application into a JAR file in the `/target` directory.

OR

```bash
mvnw clean compile quarkus:dev
```

### **Step 3: Configure AWS and Database**
Update the `application.properties` file with your AWS S3 credentials, PostgreSQL database URL, and JWT secret.

```properties
# AWS S3 Configuration
quarkus.s3.aws.region=eu-north-1
quarkus.s3.aws.credentials.type=static
quarkus.s3.aws.credentials.static-provider.access-key-id=YOUR_ACCESS_KEY
quarkus.s3.aws.credentials.static-provider.secret-access-key=YOUR_SECRET_KEY

# PostgreSQL Configuration
quarkus.datasource.jdbc.url=jdbc:postgresql://your-database-url:5432/chatservice
quarkus.datasource.username=YOUR_DB_USER
quarkus.datasource.password=YOUR_DB_PASSWORD

# JWT Configuration
quarkus.jwt.secret=your-secret-key
```

### **Step 4: Deploy to AWS**
The application can be deployed using AWS ECS, Docker, or any other cloud service.

1. **Build Docker Image:**

```bash
docker build -t quarkus-websocket-messaging .
```

2. **Push Docker Image:**
Push the image to your container registry (AWS ECR, DockerHub, etc.)

3. **Deploy on AWS ECS:**
Set up ECS cluster and service to deploy the containerized application.

### **Step 5: Test WebSocket and APIs**

### **WebSocket Testing**

1. **Send Message to Individual**
   - WebSocket URL: `ws://localhost:8080/chat/{senderUserIdOrDeviceId}?token=token`
   - Payload:
     ```json
     {
       "content":"hello",
       "recipient": "recipient-user-id",
       "messageId": "msg_{{timestamp}}"
     }
     ```

2. **Send Message to Group**
   - WebSocket URL: `ws://localhost:8080/chat/{userId}?token=token`
   - Payload:
     ```json
     {
       "content":"hello",
       "groupId": {groupId},
       "messageId": "msg_{{timestamp}}"
     }
     ```

### **API Testing**

1. **Register User**
   ```bash
   curl --location 'localhost:8080/auth/register' --header 'Content-Type: application/json' --data-raw '{
    "firstName": "Asad",
    "middleName": "Hasan",
    "lastName": "Ali",
    "email": "asad.ali@joinvivo.co",
    "mobile": "+44-7767440208",
    "password": "test@123",
    "deviceId": "87238-laptop-6268"
    }'
   ```
   Response:
    ```
   {
    "user": {
        "createdAt": "2024-10-23T14:35:36.9151068",
        "deviceId": "87238-laptop-6268",
        "email": "asad.ali@joinvivo.co",
        "firstName": "Asad",
        "id": 26,
        "lastName": "Ali",
        "middleName": "Hasan",
        "mobile": "+44-7767440208",
        "userId": "41e0a1ee-c6a4-42c9-b8f7-75986c107668",
        "userType": "REGISTERED",
        "username": "asad.ali@joinvivo.co"
    }
    }
   ```

2. **Login User**
   ```bash
   curl --location 'localhost:8080/auth/login' --header 'Content-Type: application/json' --data-raw '{
       "username": "asad.ali@joinvivo.co",
       "password": "test@123"
   }'
   ```
   Response:
    ```
   {
    "user": {
        "createdAt": "2024-10-23T14:35:36.915107",
        "deviceId": "87238-laptop-6268",
        "email": "asad.ali@joinvivo.co",
        "firstName": "Asad",
        "id": 26,
        "lastName": "Ali",
        "middleName": "Hasan",
        "mobile": "+44-7767440208",
        "userId": "41e0a1ee-c6a4-42c9-b8f7-75986c107668",
        "userType": "REGISTERED",
        "username": "asad.ali@joinvivo.co"
    },
    "token": "/zafYPurow4Vfd8Vgt+KKF3tqoJfCISaJGr60JjDphp21cMv10fMrxG7brbB08YlZKGtvaPdALMAUrNOI9+MUQ=="
    }
   ```
3. **Upload Media**
   ```bash
   curl --location 'http://localhost:8080/media/upload' --header 'Authorization: <token>' --form 'file=@"path/to/file"'
   ```

4. **Set User Preferences**
   ```bash
   curl --location 'localhost:8080/user-preferences/{userId}' --header 'Authorization: <token>' --header 'Content-Type: application/json' --data '{
       "readReceipt": "ON",
       "muteNotifications": true,
       "hideSeenStatus":true
   }'
   ```

5. **Get User Preferences**
   ```bash
   curl --location 'localhost:8080/user-preferences/41e0a1ee-c6a4-42c9-b8f7-75986c107668' \--header 'Authorization: /zafYPurow4Vfd8Vgt+KKF3tqoJfCISaJGr60JjDphp21cMv10fMrxG7brbB08YlZKGtvaPdALMAUrNOI9+MUQ==' \--data ''
   ```
    Response:
    ```
   {
    "hideSeenStatus": true,
    "muteNotifications": true,
    "readReceipt": "ON"
   }
   ```
6. **Create Group**
    ```bash
   curl --location 'localhost:8080/groups/create' \--header 'Authorization: DgZvs2rNOE1k4fZil1OMkInVhsrfQKL/cIon6H00Q1SY99ZeVaAwW880htar9KCGJ2c/OPWUt0YpUkqUV3ArUg==' \--header 'Content-Type: application/json' \--data '
   {
    "groupName": "My 1st Group",
    "users": [
    "1cc6558e-af99-4f34-b209-22efb0e5da13",
    "b0f6ddd2-aaf4-47c5-97d5-6bdd2bea4bef"
      ]
    }
   ```
7. **Get Groups**
    ```bash
    curl --location 'localhost:8080/groups/user/b0f6ddd2-aaf4-47c5-97d5-6bdd2bea4bef' \--header 'Authorization: m4c7S26q8gBSBTl4ZcjAcfvbcsKxRQJD+Lw8CzB8UaQ/CYPGvZDEX8LbzdNQYxFhZKGtvaPdALMAUrNOI9+MUQ==' \--data ''
   ```
   Response: 
    ```
   [
    {
        "groupName": "My 1st Group",
        "id": 2,
        "users": [
            "1cc6558e-af99-4f34-b209-22efb0e5da13",
            "b0f6ddd2-aaf4-47c5-97d5-6bdd2bea4bef",
            "de83e8a6-de63-4aa3-93e1-5a9499acddcf"
        ]
    },
    {
        "groupName": "My Group",
        "id": 3,
        "users": [
            "1cc6558e-af99-4f34-b209-22efb0e5da13",
            "b0f6ddd2-aaf4-47c5-97d5-6bdd2bea4bef",
            "8598c4fa-0d9f-4039-9d58-90af9c395f55",
            "de83e8a6-de63-4aa3-93e1-5a9499acddcf"
        ]
    }
    ]
   ```
8. **Get Group Messages**
    ```bash
    curl --location 'localhost:8080/chats/group/2/messages?userId=b0f6ddd2-aaf4-47c5-97d5-6bdd2bea4bef' \--header 'Authorization: m4c7S26q8gBSBTl4ZcjAcfvbcsKxRQJD+Lw8CzB8UaQ/CYPGvZDEX8LbzdNQYxFhZKGtvaPdALMAUrNOI9+MUQ=='   
   ```
   Response:
   ```
   [
    {
        "acknowledgment": false,
        "content": "hello",
        "groupId": 2,
        "id": 76,
        "mediaUrl": "quarkus-chat-service.s3.eu-north-1.amazonaws.com/14d5ce8e-3cb3-4eb3-9faf-3d3a4a85b26f.jpeg",
        "sender": "de83e8a6-de63-4aa3-93e1-5a9499acddcf",
        "timestamp": "2024-10-23T12:56:59.830732"
    },
    {
        "acknowledgment": false,
        "content": "hello",
        "groupId": 2,
        "id": 73,
        "sender": "de83e8a6-de63-4aa3-93e1-5a9499acddcf",
        "timestamp": "2024-10-23T05:44:48.832487"
    }
    ]
    ```
9. **Get Messages Between Users**
    ```bash
    curl --location 'localhost:8080/chats/messages/b0f6ddd2-aaf4-47c5-97d5-6bdd2bea4bef?target=de83e8a6-de63-4aa3-93e1-5a9499acddcf&page=0&size=10' \--header 'Authorization: m4c7S26q8gBSBTl4ZcjAcfvbcsKxRQJD+Lw8CzB8UaQ/CYPGvZDEX8LbzdNQYxFhZKGtvaPdALMAUrNOI9+MUQ=='   
   ```
   Response:
   ```
   [
    {
        "content": "hello",
        "id": 75,
        "mediaUrl": "quarkus-chat-service.s3.eu-north-1.amazonaws.com/14d5ce8e-3cb3-4eb3-9faf-3d3a4a85b26f.jpeg",
        "messageId": "msg_1729668391",
        "receiver": "de83e8a6-de63-4aa3-93e1-5a9499acddcf",
        "sender": "b0f6ddd2-aaf4-47c5-97d5-6bdd2bea4bef",
        "timestamp": "2024-10-23T12:56:31.594184"
    },
    {
        "content": "hello",
        "id": 74,
        "messageId": "msg_1729667730",
        "receiver": "de83e8a6-de63-4aa3-93e1-5a9499acddcf",
        "sender": "b0f6ddd2-aaf4-47c5-97d5-6bdd2bea4bef",
        "timestamp": "2024-10-23T12:45:30.639936"
    }
    ]
   ```
10. **Users in My  Contact**
    ```bash
    curl --location 'localhost:8080/chats/b0f6ddd2-aaf4-47c5-97d5-6bdd2bea4bef/contacts' \
    --header 'Authorization: m4c7S26q8gBSBTl4ZcjAcfvbcsKxRQJD+Lw8CzB8UaQ/CYPGvZDEX8LbzdNQYxFhZKGtvaPdALMAUrNOI9+MUQ==' \
    --data ''
    ```
    Response:
    ```[
    {
        "userId": "de83e8a6-de63-4aa3-93e1-5a9499acddcf"
    }
    ]

    ```
11. **Get Individual User Detail**
    ```bash
    curl --location 'localhost:8080/chats/de83e8a6-de63-4aa3-93e1-5a9499acddcf/contact?target=41e0a1ee-c6a4-42c9-b8f7-75986c107668' \
    --header 'Authorization: DgZvs2rNOE1k4fZil1OMkInVhsrfQKL/cIon6H00Q1SY99ZeVaAwW880htar9KCGJ2c/OPWUt0YpUkqUV3ArUg==' \
    --data ''
    ```
    Response:
    ```
    [
    {
        "firstName": "Asad",
        "lastName": "Ali",
        "middleName": "Hasan",
        "userId": "41e0a1ee-c6a4-42c9-b8f7-75986c107668"
    }
    ]
    ```
---
This Quarkus-based WebSocket messaging application is ready to be deployed and tested with real-time communication and various API endpoints.
