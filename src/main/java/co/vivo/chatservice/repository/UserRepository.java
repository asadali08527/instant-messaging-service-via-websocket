package co.vivo.chatservice.repository;

import co.vivo.chatservice.UserType;
import co.vivo.chatservice.model.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void saveUser(UserEntity user) {
        em.persist(user);
        em.flush();
    }

    @Transactional
    public void updateUser(UserEntity user) {
        em.merge(user);
    }
    @Transactional
    public Optional<UserEntity> findUserByEmailOrMobile(String emailOrMobile) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.email = :emailOrMobile OR u.mobile = :emailOrMobile", UserEntity.class)
                .setParameter("emailOrMobile", emailOrMobile)
                .getResultStream().findFirst();
    }

    @Transactional
    public Optional<UserEntity> findGuestByDeviceId(String deviceId) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.deviceId = :deviceId", UserEntity.class)
                .setParameter("deviceId", deviceId)
                .getResultStream().findFirst();
    }

    @Transactional
    public UserEntity findByIdAndUserType(String userId, UserType userType) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.userId = :userId AND u.userType = :userType", UserEntity.class)
                .setParameter("userId", userId)
                .setParameter("userType", userType)
                .getSingleResult();
    }

    @Transactional
    public UserEntity findByUserId(String userId){
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.userId = :userId", UserEntity.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }


}
