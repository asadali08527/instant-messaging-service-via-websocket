package co.vivo.chatservice.model;

import co.vivo.chatservice.UserType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;
    private String deviceId; // For guest users
    private String password; // For registered users

    private String token;
    private UserType userType;
    private LocalDateTime createdAt;

    public UserEntity(Long id, String userId, String username, String email, String mobile, String deviceId, String password, String token, UserType userType, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.deviceId = deviceId;
        this.password = password;
        this.token = token;
        this.userType = userType;
        this.createdAt = createdAt;
    }

    public UserEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserEntity(Long id, String userId, String username, String firstName, String middleName, String lastName, String email, String mobile, String deviceId, String password, String token, UserType userType, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.deviceId = deviceId;
        this.password = password;
        this.token = token;
        this.userType = userType;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", userType=" + userType +
                ", createdAt=" + createdAt +
                '}';
    }
}
