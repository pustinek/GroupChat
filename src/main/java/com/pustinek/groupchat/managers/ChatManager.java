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

        if (group == null) {
            Main.warrning("trying to send message to group that doesn't exist(groupID: " + chatObj.getGroupID() + ")!");
            return;
        }

        String playerName = chatObj.getUsername();
        String message = chatObj.getContent();


        TextComponent cmp = new TextComponent(ChatUtils.chatColor(
                generateChatPrefix(
                        Main.getConfigManager().getGroupChatPrefix(), playerName, playerName, group)));
        TextComponent textCmp = new TextComponent(message);
        textCmp.setColor(Main.getConfigManager().getChatColor());


        cmp.addExtra(textCmp);

        String hoverText =
                "┅┅┅ &6GroupChat &f┅┅┅" + "\n" +
                        "GroupName: " + group.getName() + "\n" +
                        "Username: " + playerName + "\n" +
                        "Members: " + group.getMembers().size() + "\n" +
                        "&7Click to toggle this group";

        cmp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtils.chatColor(hoverText)).create()));
        cmp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/groupchat chat " + group.getName()));


        // Main.debug("Group members::: " + group.getMembers().stream().map(GroupMember::getUsername).collect(Collectors.joining(",")));

        for (int i = 0; i < group.getMembers().size(); i++) {
            Player groupMember = Bukkit.getPlayer(group.getUUIDOfMembers().get(i));
            if (groupMember == null || !groupMember.isOnline()) continue;
            groupMember.spigot().sendMessage(cmp);
        }


        System.out.println(cmp.getText() + textCmp.getText());
    }


    public void sendGenericGroupMessage(Group group, String message) {
        if (group == null) {
            Main.warrning("trying to send a generic message to group that doesn't exist !");
            return;
        }


        TextComponent cmp = new TextComponent(ChatUtils.chatColor(
                generateChatPrefix(
                        Main.getConfigManager().getGroupChatGenericPrefix(), "", "", group)));
        TextComponent textCmp = new TextComponent(message);
        textCmp.setColor(Main.getConfigManager().getChatColor());

        cmp.addExtra(textCmp);

        for (int i = 0; i < group.getMembers().size(); i++) {
            Player groupMember = Bukkit.getPlayer(group.getUUIDOfMembers().get(i));
            if (groupMember == null || !groupMember.isOnline()) continue;
            groupMember.spigot().sendMessage(cmp);
        }


        System.out.println(cmp.getText() + textCmp.getText());

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
