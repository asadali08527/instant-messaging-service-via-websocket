package co.vivo.chatservice.repository;

import co.vivo.chatservice.model.ChatGroupEntity;
import co.vivo.chatservice.model.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ChatGroupRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(ChatGroupEntity chatGroup) {
        entityManager.persist(chatGroup);
    }

    @Transactional
    public ChatGroupEntity findById(Long id) {
        return entityManager.find(ChatGroupEntity.class, id);
    }
    @Transactional
    public List<ChatGroupEntity> findAll() {
        return entityManager.createQuery("SELECT g FROM ChatGroupEntity g", ChatGroupEntity.class).getResultList();
    }

    @Transactional
    public void addUserToGroup(Long groupId, UserEntity user) {
        ChatGroupEntity group = findById(groupId);
        if (group != null) {
            group.getUsers().add(user);
            entityManager.merge(group);
        }
    }

    @Transactional
    public void removeUserFromGroup(Long groupId, UserEntity user) {
        ChatGroupEntity group = findById(groupId);
        if (group != null) {
            group.getUsers().remove(user);
            entityManager.merge(group);
        }
    }

    @Transactional
    public List<ChatGroupEntity> findGroupsByUserId(Long userId) {
        return entityManager.createQuery("SELECT g FROM ChatGroupEntity g JOIN g.users u WHERE u.id = :userId", ChatGroupEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
