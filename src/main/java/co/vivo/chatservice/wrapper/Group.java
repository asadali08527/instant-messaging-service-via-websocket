package co.vivo.chatservice.wrapper;

import java.util.ArrayList;
import java.util.List;

public class Group {


    private Long id;

    private String groupName;


    private List<User> users = new ArrayList<>();

    public Group() {
    }

    public Group(String groupName, List<User> users) {
        this.groupName = groupName;
        this.users = users;
    }

    public Group(Long id, String groupName, List<User> users) {
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
