package com.pustinek.groupchat.models;

import java.util.UUID;

public class Chat {
    private String server;
    private String content;
    private UUID groupID;
    private UUID userID;
    private String username;


    public Chat(String content, UUID groupID, UUID userID, String username) {
        this.content = content;
        this.groupID = groupID;
        this.userID = userID;
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public UUID getGroupID() {
        return groupID;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "groupID: " + groupID + "\n" + "userID: " + userID + "\n" + "server: " + server + "\n";
    }
}
