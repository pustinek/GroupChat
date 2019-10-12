package com.pustinek.groupchat.listeners;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
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
        Player player = event.getPlayer();
        // Check if player has his group toggled, if not just return
        UUID groupId = Main.getChatManager().getPlayerActiveGroup(player.getUniqueId());

        if (groupId == null) return;

        Group group = Main.getGroupManager().getGroupClone(groupId);
        String chatPrefix = Main.getConfigManager().getChatPrefix();
        String groupFormatPrefix = Main.getConfigManager().getGroupFormatPrefix();
        Main.debug("groupPrefixFormat:  " + groupFormatPrefix);

        String groupPrefix = groupFormatPrefix.replace("{group_prefix}", group.getPrefix());

        TextComponent cmp = new TextComponent(ChatUtils.chatColor(chatPrefix + groupPrefix + event.getMessage()));

        String hoverText = "Name: " + group.getName() + "\n" +
                "Members: " + group.getMembers().size() + "\n" +
                "Click to toggle this group";

        cmp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        cmp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/groupchat set " + group.getName()));
        group.getMembers().forEach((uuid -> {
            Player groupMember = Bukkit.getPlayer(uuid);
            if (groupMember == null || !groupMember.isOnline()) return;
            groupMember.spigot().sendMessage(cmp);
        }));


        event.setCancelled(true);

    }
}




