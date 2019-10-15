package com.pustinek.groupchat.managers;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Chat;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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


    public void sendChatToGroupMembers(Chat chatObj) {

        Group group = Main.getGroupManager().getGroupClone(chatObj.getGroupID());
        String playerName = chatObj.getUsername();
        String message = chatObj.getContent();

        Main.debug("sendMessageToGroupMembers called && passe...");
        TextComponent cmp = new TextComponent(ChatUtils.chatColor(
                generateChatPrefix(
                        Main.getConfigManager().getGroupChatPrefix(), playerName, playerName, group) +
                        " " +
                        message));

        String hoverText =
                "GroupName: " + group.getName() + "\n" +
                        "Username: " + playerName + "\n" +
                        "Members: " + group.getMembers().size() + "\n" +
                        "Click to toggle this group";

        cmp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        cmp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/groupchat chat " + group.getName()));

        Integer onlineMembersCount = 0;

        for (int i = 0; i < group.getMembers().size(); i++) {
            Player groupMember = Bukkit.getPlayer(group.getMembers().get(i));
            if (groupMember == null || !groupMember.isOnline()) continue;
            onlineMembersCount++;
            groupMember.spigot().sendMessage(cmp);
        }

        if (onlineMembersCount < 2) {
            //Main.message(sender, "message-noMembersOnline");
        }

        System.out.println(cmp.getText());

    }

    public String generateChatPrefix(String text, String name, String displayName, Group group) {
        String parsedString = "";
        parsedString = text
                .replace("{user.name}", name)
                .replace("{user.displayName}", displayName)
                .replace("{group.name}", group.getName())
                .replace("{group.prefix}", group.getPrefix());
        return parsedString;
    }





}
