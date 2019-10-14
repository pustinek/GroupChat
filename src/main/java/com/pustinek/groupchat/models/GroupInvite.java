package com.pustinek.groupchat.models;

import java.sql.Timestamp;
import java.util.UUID;

public class GroupInvite {
    private Integer id;
    private UUID groupID;
    private UUID inviteeID;
    private UUID inviterID;
    private Timestamp timestamp;


    public GroupInvite(Integer id, UUID groupID, UUID inviteeID, UUID inviterID, Timestamp timestamp) {
        this.id = id;
        this.groupID = groupID;
        this.inviteeID = inviteeID;
        this.inviterID = inviterID;
        this.timestamp = timestamp;
    }

    public GroupInvite(UUID groupID, UUID inviteeID, UUID inviterID) {
        this.id = -1;
        this.groupID = groupID;
        this.inviteeID = inviteeID;
        this.inviterID = inviterID;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
