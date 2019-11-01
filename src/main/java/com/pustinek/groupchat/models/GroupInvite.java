package com.pustinek.groupchat.models;

import java.util.UUID;

public class GroupInvite {
    private Integer id;
    private UUID groupID;
    private UUID inviteeID;
    private String inviteeUsername;
    private UUID inviterID;

    public GroupInvite(Integer id, UUID groupID, UUID inviteeID, UUID inviterID, String inviteeUsername) {
        this.id = id;
        this.groupID = groupID;
        this.inviteeID = inviteeID;
        this.inviterID = inviterID;
        this.inviteeUsername = inviteeUsername;
    }

    public GroupInvite(UUID groupID, UUID inviteeID, UUID inviterID, String inviteeUsername) {
        this.id = -1;
        this.groupID = groupID;
        this.inviteeID = inviteeID;
        this.inviterID = inviterID;
        this.inviteeUsername = inviteeUsername;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getGroupID() {
        return groupID;
    }

    public UUID getInviteeID() {
        return inviteeID;
    }

    public UUID getInviterID() {
        return inviterID;
    }

    public String getInviteeUsername() {
        return inviteeUsername;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GroupInvite groupInvite = (GroupInvite) obj;
        return groupInvite.getInviteeID().equals(inviteeID) && groupInvite.getGroupID().equals(groupID);
    }
}
