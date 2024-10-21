package co.vivo.chatservice.service;

import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.MessageRepository;
import co.vivo.chatservice.wrapper.ChatMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Handles chat operations including direct and group messaging.
 * Author: Asad Ali
 */
@ApplicationScoped
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private ChatGroupService chatGroupService;

    @Inject
    private MessageService messageService;

    /**
     * Handles sending a direct message between users.
     */
    public void handleDirectMessage( ChatMessage chatMessage, Session session, ConcurrentHashMap<String, Session> sessions) {
        if (chatMessage.getContent() != null) {
            messageService.sendMessage(chatMessage, session, sessions);
        }
        if (chatMessage.getMediaUrl() != null) {
            messageService.sendMessage(chatMessage, session, sessions);
        }
        messageService.saveMessage(chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getContent(), chatMessage.getMediaUrl());
    }

    /**
     * Handles sending a message to a group.
     */
    public void handleGroupMessage(ChatMessage chatMessage, Session session, ConcurrentHashMap<String, Session> sessions) {
        List<UserEntity> groupUsers = chatGroupService.getGroupUsersbyGroupId(chatMessage.getGroupId(), chatMessage.getSender());
        if (groupUsers.stream().map(UserEntity::getUserId).collect(Collectors.toList()).contains(chatMessage.getSender())) {
            groupUsers.forEach(user -> {
                if (!user.getUserId().equalsIgnoreCase(chatMessage.getSender())) {
                    chatMessage.setRecipient(user.getUserId());
                    messageService.sendMessage(chatMessage, session, sessions);
                }
            });
            messageService.saveGroupMessage(chatMessage.getSender(), chatMessage.getGroupId(), chatMessage.getContent(), chatMessage.getMediaUrl());
        } else {
            throw new RuntimeException("User not part of the group");
        }
    }

    /**
     * Broadcasts a message to all connected users except the sender.
     */
    public void broadcastMessage(ChatMessage chatMessage, Session session, ConcurrentHashMap<String, Session> sessions) {
        String messageToBroadcast = messageService.serializeMessage(chatMessage);
        sessions.values().forEach(s -> {
            if (!s.equals(session)) {
                s.getAsyncRemote().sendText(messageToBroadcast);
            }
        });
    }

    /**
     * Notifies users when a new user joins.
     */
    public void notifyUserJoined(String userId, Session session, ConcurrentHashMap<String, Session> sessions) {
        sessions.values().forEach(s -> s.getAsyncRemote().sendText("User " + userId + " has joined the chat"));
    }

    /**
     * Notifies users when a user leaves the chat.
     */
    public void notifyUserLeft(String userId, ConcurrentHashMap<String, Session> sessions) {
        sessions.values().forEach(s -> s.getAsyncRemote().sendText("User " + userId + " has left the chat"));
    }
}
