package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.managers.ChatManager;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandChat extends CommandDefault {

    private final Main plugin;

    public CommandChat(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat chat";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.GROUP_CHAT))
            return "help-chat";
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Boolean set = null;
        if (player == null) {
            // Command was triggered via the console,
            Main.message(sender, "cmd-onlyByPlayer");
            return;
        }
        if (!sender.hasPermission(Permissions.GROUP_CHAT)) {
            // The player (Sender) doesn't have the required permissions, notify him
            Main.message(sender, "chat-noPermission");
            return;
        }

        if (args.length < 2) {
            Main.message(sender, "chat-help");
            return;
        }
        if (args.length == 3) {
            String val = args[2];
            if (val.equals("1")
                    || val.equalsIgnoreCase("true")
                    || val.equalsIgnoreCase("t")
            ) {
                set = true;
            } else {
                set = false;
            }
        }

        String groupName = args[1];

        Group group = Main.getGroupManager().getGroupByNameAndMember(groupName, player.getUniqueId());

        if (group == null) {
            Main.message(sender, "group-notExistOrNotOwner");
            return;
        }
        ChatManager cm = Main.getChatManager();

        UUID playerID = player.getUniqueId();
        UUID activeGroupID = cm.getPlayerActiveGroup(playerID);

        if (activeGroupID != null && activeGroupID != group.getId()) {
            cm.setPlayerActiveGroup(playerID, group.getId());
            Main.message(sender, "chat-setSuccess", groupName);
            return;
        }


        if (activeGroupID == null && set == null) {
            cm.setPlayerActiveGroup(playerID, group.getId());
            Main.message(sender, "chat-setSuccess", groupName);
        } else if (activeGroupID != null && set == null) {
            cm.unsetPlayerActiveGroup(playerID);
            Main.message(sender, "chat-unsetSuccess", groupName);
        } else {
            if (set) {
                cm.setPlayerActiveGroup(playerID, group.getId());
                Main.message(sender, "chat-setSuccess", groupName);
            } else {
                cm.unsetPlayerActiveGroup(playerID);
                Main.message(sender, "chat-unsetSuccess", groupName);
            }
        }
    }


    @Override
    public List<String> getTabCompleteList(int toComplete, String[] start, CommandSender sender) {
        List<String> result = new ArrayList<>();

        Player player = (Player) sender;

        if (player == null) {
            return result;
        }

        if (toComplete == 2) {
            if (sender.hasPermission(Permissions.GROUP_CHAT)) {
                for (Group group : Main.getGroupManager().getMemberGroups(player.getUniqueId())) {
                    result.add(group.getName());
                }
            }
        }

        return result;
    }
}
