package co.vivo.chatservice.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {

    private Long id;
    private String recipient;
    private String sender;
    private String content;
    private Long groupId;
    private String mediaUrl;

    private LocalDateTime timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String recipient, String sender, String content, Long groupId, String mediaUrl) {
        this.recipient = recipient;
        this.sender = sender;
        this.content = content;
        this.groupId = groupId;
        this.mediaUrl = mediaUrl;
    }

    public ChatMessage(Long id, String recipient, String sender, String content, Long groupId, String mediaUrl, LocalDateTime timestamp) {
        this.id = id;
        this.recipient = recipient;
        this.sender = sender;
        this.content = content;
        this.groupId = groupId;
        this.mediaUrl = mediaUrl;
        this.timestamp = timestamp;
    }

    public ChatMessage(Long id, String recipient, String sender, String content, String mediaUrl, LocalDateTime timestamp) {
        this.id = id;
        this.recipient = recipient;
        this.sender = sender;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.timestamp = timestamp;
    }


    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
