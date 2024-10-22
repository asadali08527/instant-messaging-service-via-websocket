package co.vivo.chatservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages",indexes = {
        @Index(columnList = "sender"),
        @Index(columnList = "receiver"),
        @Index(columnList = "groupId")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"messageId", "sender", "receiver"})
})
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String messageId;
    private String sender;
    private Long groupId;
    private String receiver;
    private String content;
    private String mediaUrl;
    private LocalDateTime timestamp;
    public MessageEntity() {
    }

    public MessageEntity(Long id, String sender, Long groupId, String receiver, String content, String mediaUrl, LocalDateTime timestamp) {
        this.id = id;
        this.sender = sender;
        this.groupId = groupId;
        this.receiver = receiver;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
