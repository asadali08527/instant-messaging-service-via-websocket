package co.vivo.chatservice.controller;

import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.service.AuthService;
import co.vivo.chatservice.repository.MessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/v2/chat/{userId}")
@ApplicationScoped
public class ChatSocketV2 {
    Logger logger = LoggerFactory.getLogger(ChatSocketV2.class);

    @Inject
    AuthService authService;

    @Inject
    MessageRepository messageRepository;

    private ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) throws IOException {
        CompletableFuture.runAsync(() -> {
            UserEntity user = null;
            String token = null;

            // Extract query parameters from the URI (including token)
            String query = session.getQueryString(); // Get the query part of the URI
            logger.info("Query :{} ", query);
            if (query != null && !query.isEmpty()) {
                // Parse the token from the query string (e.g., ?token=abc123)
                for (String param : query.split("&")) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                        token = keyValue[1];
                        break;
                    }
                }
            }

            if (token != null && !token.isEmpty()) {
                // Validate token and get the user
                user = authService.verifyToken(token);
                if (user == null || !user.getUserId().equals(userId)) {
                    try {
                        session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid token or user mismatch"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
            } else {
                // Guest user flow
                user = authService.guestLogin(userId);
            }
            logger.info("User :{} ", user.toString());
            // If the user is authenticated (either guest or registered), store the session
            if (user != null) {
                sessions.put(userId, session);
                broadcast(session,"User :" + user.getUsername() + " joined the chat");
            } else {
                try {
                    session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Authentication failed"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        CompletableFuture.runAsync(() -> {
            MessageEntity msgEntity = new MessageEntity();
            msgEntity.setContent(message);
            msgEntity.setTimestamp(LocalDateTime.now());
            messageRepository.saveMessage(msgEntity);
            logger.info("Message :{} saved", message.toString());
        });
        // Broadcast message to all connected users
        broadcast(session, message);
    }
    private void broadcast(String message) {
        sessions.values().forEach(s -> s.getAsyncRemote().sendText(message));
    }

    private void broadcast(Session session, String message) {
        session.getAsyncRemote().sendText(message);
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(userId);
        broadcast("User :"+userId+" left the chat");

    }
}

