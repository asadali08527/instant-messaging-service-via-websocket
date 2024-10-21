package co.vivo.chatservice.controller;

import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.service.AuthService;
import co.vivo.chatservice.service.ChatService;
import co.vivo.chatservice.util.ChatUtil;
import co.vivo.chatservice.wrapper.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket chat handler that manages user sessions and messaging between users and groups.
 * Author: Asad Ali
 */
@ServerEndpoint("/chat/{userId}")
@ApplicationScoped
public class ChatSocket {

    private static final Logger logger = LoggerFactory.getLogger(ChatSocket.class);

    @Inject
    private AuthService authService;

    @Inject
    private ChatService chatService;  // New service for handling chat logic

    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON processing

    /**
     * Called when a new WebSocket connection is opened.
     * Authenticates the user based on token or device ID.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        CompletableFuture.runAsync(() -> {
            try {
                UserEntity user = authenticateUser(session, userId);
                if (user != null) {
                    sessions.put(userId, session);
                    chatService.notifyUserJoined(userId, session, sessions);
                } else {
                    closeSession(session, "Authentication failed");
                }
            } catch (Exception e) {
                logger.error("Error during WebSocket connection setup: {}", e.getMessage());
            }
        });
    }

    /**
     * Handles incoming messages, differentiating between direct and group messages.
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        CompletableFuture.runAsync(() -> {
            try {
                ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
                String senderId = getUserIdFromSession(session);
                chatMessage.setSender(senderId);
                if (chatMessage.getRecipient() != null) {
                    chatService.handleDirectMessage(chatMessage, session, sessions);
                } else if (chatMessage.getGroupId() != null) {
                    chatService.handleGroupMessage(chatMessage, session, sessions);
                } else {
                    chatService.broadcastMessage(chatMessage, session, sessions);
                }
            } catch (IOException e) {
                logger.error("Failed to parse message: {}", e.getMessage());
            }
        });
    }

    /**
     * Called when a WebSocket connection is closed.
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(userId);
        chatService.notifyUserLeft(userId, sessions);
    }

    /**
     * Closes the session with a given reason.
     */
    private void closeSession(Session session, String reason) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, reason));
        } catch (IOException e) {
            logger.error("Error while closing session: {}", e.getMessage());
        }
    }

    /**
     * Authenticates the user based on token or guest login.
     */
    private UserEntity authenticateUser(Session session, String userId) {
        String token = ChatUtil.extractToken(session.getQueryString());
        if (token != null && !token.isEmpty()) {
            return authService.verifyToken(token);
        } else {
            return authService.guestLogin(userId);
        }
    }

    /**
     * Gets the user ID from the WebSocket session.
     */
    private String getUserIdFromSession(Session session) {
        return sessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);
    }
}
