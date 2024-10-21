package co.vivo.chatservice.repository;

import co.vivo.chatservice.model.MessageEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class MessageRepository {

    Logger logger = LoggerFactory.getLogger(MessageRepository.class);

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void saveMessage(MessageEntity message) {
        logger.info("Message :{}", message.toString());
        em.persist(message);
        em.flush();
    }

    public List<MessageEntity> getMessageHistory(int limit) {
        TypedQuery<MessageEntity> query = em.createQuery("SELECT m FROM MessageEntity m ORDER BY m.timestamp DESC", MessageEntity.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Transactional
    public MessageEntity updateMessage(Long id, String newContent) {
        MessageEntity message = em.find(MessageEntity.class, id);
        if (message != null) {
            message.setContent(newContent);
            message.setTimestamp(LocalDateTime.now());
            em.merge(message);
            return message;
        }
        return message;
    }

    @Transactional
    public List<MessageEntity> getMessagesBetweenUsers(String user1, String user2, int page, int size) {
        return em.createQuery("SELECT m FROM MessageEntity m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.timestamp DESC", MessageEntity.class)
                .setParameter("user1", user1)
                .setParameter("user2", user2)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Transactional
    public List<MessageEntity> getMessagesInGroup(Long groupId, int page, int size) {
        return em.createQuery("SELECT m FROM MessageEntity m WHERE m.groupId = :groupId ORDER BY m.timestamp DESC", MessageEntity.class)
                .setParameter("groupId", groupId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Transactional
    public List<MessageEntity> getMessagesBetweenUsersInTimeRange(String user1, String user2, LocalDateTime startTime, LocalDateTime endTime, int page, int size) {
        return em.createQuery("SELECT m FROM MessageEntity m WHERE ((m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1)) AND m.timestamp BETWEEN :startTime AND :endTime ORDER BY m.timestamp DESC", MessageEntity.class)
                .setParameter("user1", user1)
                .setParameter("user2", user2)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}
