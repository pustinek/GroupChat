package com.pustinek.groupchat.listeners;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.managers.RedisManager;
import com.pustinek.groupchat.models.Chat;
import com.pustinek.groupchat.utils.DeleteConformation;
import me.wiefferink.interactivemessenger.processing.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;


public class PlayerChatListener implements Listener {

    private final Main plugin;

    public PlayerChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void asyncPlayerChatListener(AsyncPlayerChatEvent event) {

        if (!event.isAsynchronous()) return;
        Player player = event.getPlayer();


        if (DeleteConformation.deleteConformationHashMap.containsKey(player.getUniqueId())) {
            Message.useFancyMessages(false);
            Main.getDeleteConformation().validate(event.getMessage(), player);
            Message.useFancyMessages(true);
            event.setCancelled(true);
            return;
        }


        // Check if player has his group toggled, if not just return
        UUID groupId = Main.getChatManager().getPlayerActiveGroup(player.getUniqueId());
        if (groupId == null) return;
        // Check if group exists before sending the message, since the group might have been removed before
        if (Main.getGroupManager().getGroupClone(groupId) == null) {
            Main.getChatManager().unsetPlayerActiveGroup(player.getUniqueId());
            return;
        }

        // Create chat object for this server chat and bungeecord chats...
        Chat chatObj = new Chat(event.getMessage(), groupId, player.getUniqueId(), player.getDisplayName());
        chatObj.setServer(Main.getConfigManager().getRedisConfig().getServer());

        Main.getChatManager().sendChatToGroupMembers(chatObj);

        String json = Main.gson.toJson(chatObj);

        Main.getRedisManager().publish(RedisManager.RedisChannels.CHAT.getValue(), json);

        event.setCancelled(true);
    }
}




