package com.pustinek.groupchat.models;

import java.util.UUID;

public class CachedPlayer {
    private UUID uuid;
    private String username;

    public CachedPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
