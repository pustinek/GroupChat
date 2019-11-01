package com.pustinek.groupchat.models;

import com.pustinek.groupchat.enums.GroupMemberRoles;

import java.util.UUID;

public class GroupMember {

    private String username;
    private UUID uuid;
    private GroupMemberRoles role;

    public GroupMember(String username, UUID uuid, GroupMemberRoles role) {
        this.username = username;
        this.uuid = uuid;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public GroupMemberRoles getRole() {
        return role;
    }
}
