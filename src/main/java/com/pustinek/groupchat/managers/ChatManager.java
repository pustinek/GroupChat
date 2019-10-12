package com.pustinek.groupchat.managers;

import com.pustinek.groupchat.Main;

import java.util.HashMap;
import java.util.UUID;

public class ChatManager extends Manager {
    private final Main plugin;
    private HashMap<UUID, UUID> playerActiveGroups = new HashMap<>();


    public ChatManager(Main plugin) {
        this.plugin = plugin;
    }


    public UUID getPlayerActiveGroup(UUID uuid) {
        return playerActiveGroups.get(uuid);
    }


    public void setPlayerActiveGroup(UUID playerUUID, UUID groupUUID) {
        playerActiveGroups.put(playerUUID, groupUUID);
    }

    public void unsetPlayerActiveGroup(UUID playerUUID) {
        playerActiveGroups.remove(playerUUID);
    }


}
