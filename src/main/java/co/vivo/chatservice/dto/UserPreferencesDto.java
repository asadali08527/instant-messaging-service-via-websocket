package co.vivo.chatservice.dto;
/**
 * DTO for updating user preferences.
 */
public class UserPreferencesDto {

    private boolean readReceiptEnabled;
    private boolean muteNotifications;
    private boolean hideSeenStatus;

    public UserPreferencesDto() {
    }

    public boolean isReadReceiptEnabled() {
        return readReceiptEnabled;
    }

    public void setReadReceiptEnabled(boolean readReceiptEnabled) {
        this.readReceiptEnabled = readReceiptEnabled;
    }

    public boolean isMuteNotifications() {
        return muteNotifications;
    }

    public void setMuteNotifications(boolean muteNotifications) {
        this.muteNotifications = muteNotifications;
    }

    public boolean isHideSeenStatus() {
        return hideSeenStatus;
    }

    public void setHideSeenStatus(boolean hideSeenStatus) {
        this.hideSeenStatus = hideSeenStatus;
    }
}
