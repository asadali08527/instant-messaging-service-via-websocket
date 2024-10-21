package co.vivo.chatservice.service;

import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.repository.MessageRepository;
import co.vivo.chatservice.wrapper.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to handle message sending and storage.
 * Author: Asad Ali
 */
@ApplicationScoped
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    MessageRepository messageRepository;

    public List<MessageEntity> getMessagesBetweenUsers(String user1, String user2, int page, int size) {
        return messageRepository.getMessagesBetweenUsers(user1, user2, page, size);
    }

    public List<MessageEntity> getMessagesInGroup(Long groupId, int page, int size) {
        return messageRepository.getMessagesInGroup(groupId, page, size);
    }

    public List<MessageEntity> getMessagesBetweenUsersInTimeRange(String user1, String user2, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        return messageRepository.getMessagesBetweenUsersInTimeRange(user1, user2, startTime, endTime, page, size);
    }

    /**
     * Sends a message from one user to another.
     */
    public void sendMessage(ChatMessage chatMessage, Session senderSession, ConcurrentHashMap<String, Session> sessions) {
        Session recipientSession = sessions.get(chatMessage.getRecipient());
        if (recipientSession != null) {
            recipientSession.getAsyncRemote().sendText(serializeMessage(chatMessage));
        } else {
            senderSession.getAsyncRemote().sendText("User " + chatMessage.getRecipient() + " is not connected.");
        }
    }
    /**
     * Saves a direct message to the database.
     */
    public void saveMessage(String senderId, String recipientId, String content, String mediaUrl) {
        MessageEntity msgEntity = new MessageEntity();
        msgEntity.setSender(senderId);
        msgEntity.setReceiver(recipientId);
        msgEntity.setContent(content);
        msgEntity.setMediaUrl(mediaUrl);
        msgEntity.setTimestamp(LocalDateTime.now());
        messageRepository.saveMessage(msgEntity);
        logger.info("Message saved: {}", content);
    }

    /**
     * Saves a group message to the database.
     */
    public void saveGroupMessage(String senderId, Long groupId, String content, String mediaUrl) {
        MessageEntity msgEntity = new MessageEntity();
        msgEntity.setSender(senderId);
        msgEntity.setGroupId(groupId);
        msgEntity.setContent(content);
        msgEntity.setMediaUrl(mediaUrl);
        msgEntity.setTimestamp(LocalDateTime.now());
        messageRepository.saveMessage(msgEntity);
        logger.info("Group message saved: {}", content);
    }

    /**
     * Serializes a ChatMessage object to a JSON string.
     */
    public String serializeMessage(ChatMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            logger.error("Error serializing message: {}", e.getMessage());
            return "";
        }
    }
}
