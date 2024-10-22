package co.vivo.chatservice.repository;

import co.vivo.chatservice.model.MessageStatusEntity;
import co.vivo.chatservice.model.MessageEntity;
import co.vivo.chatservice.model.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Repository to handle message status-related database operations.
 * Author: Asad Ali
 */
@ApplicationScoped
public class MessageStatusRepository {

    private static final Logger logger = LoggerFactory.getLogger(MessageStatusRepository.class);

    @PersistenceContext
    private EntityManager em;

    /**
     * Saves a new message status entity.
     */
    @Transactional
    public void saveMessageStatus(MessageStatusEntity status) {
        logger.info("Saving message status: {}", status);
        em.persist(status);
        em.flush();
    }

    /**
     * Retrieves message statuses for a given message.
     */
    public List<MessageStatusEntity> findStatusesByMessage(MessageEntity message) {
        TypedQuery<MessageStatusEntity> query = em.createQuery(
                "SELECT ms FROM MessageStatusEntity ms WHERE ms.message = :message", MessageStatusEntity.class);
        query.setParameter("message", message);
        return query.getResultList();
    }

    /**
     * Retrieves a specific message status for a user and message.
     */
    public MessageStatusEntity findStatus(MessageEntity message, UserEntity user) {
        TypedQuery<MessageStatusEntity> query = em.createQuery(
                "SELECT ms FROM MessageStatusEntity ms WHERE ms.message = :message AND ms.user = :user", MessageStatusEntity.class);
        query.setParameter("message", message);
        query.setParameter("user", user);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Updates the delivered status of a message for a user.
     */
    @Transactional
    public void updateDeliveredStatus(MessageEntity message, UserEntity user, boolean delivered) {
        logger.info("Inside updateDeliveredStatus for message {} and user {}", message.getId(), user.getUserId());
        MessageStatusEntity status = findStatus(message, user);
        if (status == null) {
            status = new MessageStatusEntity(message, user);
            status.setDelivered(delivered);
            status.setTimestampDelivered(delivered ? java.time.LocalDateTime.now() : null);
            saveMessageStatus(status);
        } else {
            status.setDelivered(delivered);
            status.setTimestampDelivered(delivered ? java.time.LocalDateTime.now() : null);
            em.merge(status);
        }
    }

    /**
     * Updates the read status of a message for a user.
     */
    @Transactional
    public void updateReadStatus(MessageEntity message, UserEntity user, boolean read) {
        logger.info("Inside updateReadStatus for message {} and user {}", message.getId(), user.getUserId());
        MessageStatusEntity status = findStatus(message, user);
        if (status != null) {
            status.setRead(read);
            status.setTimestampRead(read ? java.time.LocalDateTime.now() : null);
            em.merge(status);
        } else {
            status = new MessageStatusEntity(message, user);
            status.setRead(read);
            status.setTimestampRead(read ? java.time.LocalDateTime.now() : null);
            saveMessageStatus(status);
        }
    }

    /**
     * Retrieves all message statuses for a user.
     */
    public List<MessageStatusEntity> findStatusesByUser(UserEntity user) {
        TypedQuery<MessageStatusEntity> query = em.createQuery(
                "SELECT ms FROM MessageStatusEntity ms WHERE ms.user = :user", MessageStatusEntity.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Transactional
    public void updateSentStatus(MessageEntity message, UserEntity user, boolean sent) {
        MessageStatusEntity status = findStatus(message, user);
        logger.info("Inside updateSentStatus for message {} and user {}, status {}", message.getId(), user.getUserId(), status);
        if (status != null) {
            status.setSent(sent);
            status.setTimestampSent(sent ? java.time.LocalDateTime.now() : null);
            em.merge(status);
        } else {
            status = new MessageStatusEntity(message, user);
            status.setSent(sent);
            status.setTimestampSent(sent ? java.time.LocalDateTime.now() : null);
            saveMessageStatus(status);
        }
    }
}

