package co.vivo.chatservice.dto;


import java.util.ArrayList;
import java.util.List;

public class GroupRequestDto {


    private Long id;

    private String groupName;


    private List<String> users = new ArrayList<>();

    public GroupRequestDto() {
    }

    public GroupRequestDto(String groupName, List<String> users) {
        this.groupName = groupName;
        this.users = users;
    }

    public GroupRequestDto(Long id, String groupName, List<String> users) {
        this.id = id;
        this.groupName = groupName;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}

