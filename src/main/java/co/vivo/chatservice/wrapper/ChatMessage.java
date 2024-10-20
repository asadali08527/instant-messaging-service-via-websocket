package co.vivo.chatservice.wrapper;

public class ChatMessage {
    private String recipientId;
    private String content;
    private Long groupId;
    private String mediaUrl;

    public ChatMessage() {
    }

    public ChatMessage(String recipientId, String content, Long groupId, String mediaUrl) {
        this.recipientId = recipientId;
        this.content = content;
        this.groupId = groupId;
        this.mediaUrl = mediaUrl;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
