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

    // Additional methods to find messages, delete, etc.
}
