package com.pustinek.groupchat.listeners;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;


public class AsyncPlayerChatListener implements Listener {

    private final Main plugin;

    public AsyncPlayerChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void asyncPlayerChatListener(AsyncPlayerChatEvent event) {

        if (!event.isAsynchronous()) return;
        Player player = event.getPlayer();

        // Check if player has his group toggled, if not just return
        UUID groupId = Main.getChatManager().getPlayerActiveGroup(player.getUniqueId());
        if (groupId == null) return;

        // Create chat object for this server chat and bungeecord chats...
        Chat chatObj = new Chat(event.getMessage(), groupId, player.getUniqueId(), player.getDisplayName());
        chatObj.setServer(Main.getConfigManager().getRedisConfig().getServer());

        Main.getChatManager().sendChatToGroupMembers(chatObj);

        String json = Main.gson.toJson(chatObj);

        Main.getRedisManager().publish("groupchat_chat", json);

        event.setCancelled(true);
    }
}




