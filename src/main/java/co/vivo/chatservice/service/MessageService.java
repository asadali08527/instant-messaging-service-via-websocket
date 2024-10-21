package co.vivo.chatservice.service;

import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.repository.MessageRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class MessageService {

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
}
