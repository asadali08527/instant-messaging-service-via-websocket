package co.vivo.chatservice.controller;

import co.vivo.chatservice.model.ChatGroupEntity;
import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.ChatGroupRepository;
import co.vivo.chatservice.repository.MessageRepository;
import co.vivo.chatservice.service.AuthService;
import co.vivo.chatservice.service.ChatGroupService;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{userId}")
@ApplicationScoped
public class ChatSocket {
    Logger logger = LoggerFactory.getLogger(ChatSocket.class);

    @Inject
    AuthService authService;

    @Inject
    MessageRepository messageRepository;

    @Inject
    ChatGroupRepository chatGroupRepository;

    @Inject
    ChatGroupService chatGroupService;

    private ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper(); // JSON ObjectMapper


    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) throws IOException {
        CompletableFuture.runAsync(() -> {
            UserEntity user = authService.verifyToken(ChatUtil.extractToken(session.getQueryString()));
            if (user != null && user.getUserId().equals(userId)) {
                sessions.put(userId, session);
                broadcast(session, "User: " + user.getUsername() + " joined the chat");
            } else {
                closeSession(session, "Authentication failed");
            }
        });
    }
    @OnMessage
    public void onMessage(String message, Session session) {
        CompletableFuture.runAsync(() -> {
            try {
                ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
                String senderId = getUserIdFromSession(session);

                // Handle text and media messages
                if (chatMessage.getRecipientId() != null) {
                    // Direct message
                    logger.info("Handling Direct Message: {}", message);
                    handleDirectMessage(senderId, chatMessage, session);
                } else if (chatMessage.getGroupId() != null) {
                    // Group message
                    logger.info("Handling Group Message: {}", message);
                    handleGroupMessage(senderId, chatMessage, session);
                } else {
                    broadcast(session, message); // Fallback to broadcasting if message format is incorrect
                }
            } catch (IOException e) {
                logger.error("Failed to parse message: {}", e.getMessage());
            }
        });
    }

    private void handleDirectMessage(String senderId, ChatMessage chatMessage, Session session) {
        if (chatMessage.getContent() != null && !chatMessage.getContent().isEmpty()) {
            sendMessage(senderId, chatMessage.getRecipientId(), chatMessage.getContent(), session);
        }
        if (chatMessage.getMediaUrl() != null && !chatMessage.getMediaUrl().isEmpty()) {
            sendMessage(senderId, chatMessage.getRecipientId(), chatMessage.getMediaUrl(), session);
        }
        saveMessage(senderId, chatMessage.getRecipientId(), chatMessage.getContent(), chatMessage.getMediaUrl());
    }

    private void handleGroupMessage(String senderId, ChatMessage chatMessage, Session session) {
        if (chatMessage.getContent() != null && !chatMessage.getContent().isEmpty()) {
            logger.info("Handling Group Content Message: {}", chatMessage.getContent());
            sendGroupMessage(senderId, chatMessage.getGroupId(), chatMessage.getContent(), session);
        }
        if (chatMessage.getMediaUrl() != null && !chatMessage.getMediaUrl().isEmpty()) {
            logger.info("Handling Group MediaUrl: {}", chatMessage.getMediaUrl());
            sendGroupMessage(senderId, chatMessage.getGroupId(), chatMessage.getMediaUrl(), session);
        }
        saveGroupMessage(senderId, chatMessage.getGroupId(), chatMessage.getContent(), chatMessage.getMediaUrl());
    }
    private void sendGroupMessage(String senderId, Long groupId, String message, Session session) {
        // Get users in the group
        List<UserEntity> groupUsers = getGroupUsers(groupId, senderId);
        logger.info("{} users in group {} ", groupUsers.size(), groupId);
        for (UserEntity user : groupUsers) {
            if(!user.getUserId().equalsIgnoreCase(senderId)) {
                logger.info("Sending Group Content Message to: {}", user.getUserId());
                sendMessage(senderId, user.getUserId(), message, session);
            }
        }
    }
    private void saveGroupMessage(String senderId, Long groupId, String content, String mediaUrl) {
        if (senderId != null) {
            MessageEntity msgEntity = new MessageEntity();
            msgEntity.setSender(senderId);
            // Assuming you have a method to retrieve group users, you can save the group ID too
            msgEntity.setGroupId(groupId);
            msgEntity.setContent(content);
            msgEntity.setMediaUrl(mediaUrl);
            msgEntity.setTimestamp(LocalDateTime.now());
            messageRepository.saveMessage(msgEntity);
            logger.info("Group message: {} saved sent by user: {}", content, senderId);
        }
    }
    private List<UserEntity> getGroupUsers(Long groupId, String senderId) {
        logger.info("Fetching group users for group: {} user: {}", groupId, senderId);
        return chatGroupService.getGroupUsersbyGroupId(groupId, senderId);
    }
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        CompletableFuture.runAsync(() -> {
//            try {
//                // Parse the incoming JSON message
//                ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
//                String senderId = getUserIdFromSession(session);
//
//                // Handle text and media messages
//                if (chatMessage.getRecipientId() != null) {
//                    // If there's content, it's a text message
//                    if (chatMessage.getContent() != null && !chatMessage.getContent().isEmpty() ) {
//                        sendMessage(senderId, chatMessage.getRecipientId(), chatMessage.getContent(), session);
//                    }
//                    // If there's a media URL, send it as a media message
//                    if (chatMessage.getMediaUrl() != null && !chatMessage.getMediaUrl().isEmpty()) {
//                        sendMessage(senderId, chatMessage.getRecipientId(), chatMessage.getMediaUrl(), session);
//                    }
//                    saveMessage(senderId, chatMessage.getRecipientId(), chatMessage.getContent(), chatMessage.getMediaUrl());
//                } else {
//                    broadcast(session, message); // Fallback to broadcasting if message format is incorrect
//                }
//            } catch (IOException e) {
//                logger.error("Failed to parse message: {}", e.getMessage());
//            }
//        });
//    }
    private void sendMessage(String senderId, String recipientId, String directMessage, Session senderSession) {
        //Session recipientSession = getSessionFromUserId(recipientId);
        Session recipientSession = sessions.get(recipientId);
        if (recipientSession != null) {
            recipientSession.getAsyncRemote().sendText("Direct message from " + senderId + ": " + directMessage);
        }else {
                senderSession.getAsyncRemote().sendText("User " + recipientId + " is not connected.");
        }
    }

    private String getUserIdFromSession(Session session) {
        // Get the user ID from the session (this is just an example, adapt as needed)
        return sessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);
    }
    private Session getSessionFromUserId(String userId) {
        // Get the user ID from the session (this is just an example, adapt as needed)
        return sessions.entrySet().stream()
                .filter(entry -> entry.getKey().equals(userId))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse(null);
    }


    private void sendMessageToUser(String recipientId, String message, Session senderSession) {
        Session recipientSession = sessions.get(recipientId);
        if (recipientSession != null) {
            recipientSession.getAsyncRemote().sendText("Direct message from " + getUserIdFromSession(senderSession) + ": " + message);
        } else {
            senderSession.getAsyncRemote().sendText("User " + recipientId + " is not connected.");
        }
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> s.getAsyncRemote().sendText(message));
    }

    private void broadcast(Session session, String message) {
        session.getAsyncRemote().sendText(message);
    }

    private void closeSession(Session session, String reason) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, reason));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(userId);
        broadcast("User: " + userId + " left the chat");
    }

    private void saveMessage(String senderId, String recipientId, String content, String mediaUrl) {
        if (senderId != null) {
            MessageEntity msgEntity = new MessageEntity();
            msgEntity.setSender(senderId);
            msgEntity.setReceiver(recipientId);
            msgEntity.setContent(content);
            msgEntity.setMediaUrl(mediaUrl);
            msgEntity.setTimestamp(LocalDateTime.now());
            messageRepository.saveMessage(msgEntity);
            logger.info("Message: {} saved sent by user: {}", content, senderId);
        }
    }
}
