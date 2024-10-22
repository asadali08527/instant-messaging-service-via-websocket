package co.vivo.chatservice.service;

import co.vivo.chatservice.enums.UserType;
import co.vivo.chatservice.dto.UserDto;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;


    public UserEntity registerUser(UserDto userRequestDto) {
        UserEntity userEntity = new UserEntity();

        // Generate userId using UUID
        String userId = UUID.randomUUID().toString();
        userEntity.setUserId(userId);

        // Determine if the user is a guest or a registered user based on the presence of email or mobile
        if (userRequestDto.getEmail() != null && !userRequestDto.getEmail().isEmpty()) {
            userEntity.setEmail(userRequestDto.getEmail());
            if (userRequestDto.getEmail() != null && !userRequestDto.getEmail().isEmpty())
                userEntity.setUsername(userRequestDto.getEmail());  // Use email as username if provided
            userEntity.setUserType(UserType.REGISTERED);
            userEntity.setPassword(userRequestDto.getPassword());
        } else if (userRequestDto.getMobile() != null && !userRequestDto.getMobile().isEmpty()) {
            userEntity.setMobile(userRequestDto.getMobile());
            if (userEntity.getUsername() == null && userRequestDto.getMobile() != null && !userRequestDto.getMobile().isEmpty())
                userEntity.setUsername(userRequestDto.getMobile());  // Use mobile as username if provided
            userEntity.setPassword(userRequestDto.getPassword());
            userEntity.setUserType(UserType.REGISTERED);
        } else {
            // If both email and mobile are absent, it's a guest user
            userEntity.setUserId(userRequestDto.getDeviceId());
            userEntity.setUsername(userRequestDto.getDeviceId()); // Use deviceId as username for guest
            userEntity.setUserType(UserType.GUEST);
        }

        // Set other fields from the request
        userEntity.setDeviceId(userRequestDto.getDeviceId());
        userEntity.setCreatedAt(LocalDateTime.now());

        // Save the user entity to the database
        userRepository.saveUser(userEntity);
        return userEntity;
    }

    public UserEntity getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
}