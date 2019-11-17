package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupMember;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandLeave extends CommandDefault {
    @Override
    public String getCommandStart() {
        return "groupchat leave";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.GROUP_LEAVE))
            return "help-leave";
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (player == null) {
            // Command was triggered via the console,
            Main.message(sender, "cmd-onlyByPlayer");
            return;
        }

        if (!sender.hasPermission(Permissions.GROUP_LEAVE)) {
            Main.message(sender, "leave-noPermission");
            return;
        }

        if (args.length < 2) {
            Main.message(sender, "leave-help");
            return;
        }

        String groupName = args[1];

        Group group = Main.getGroupManager().getGroupByNameAndMember(groupName, player.getUniqueId());

        if (group == null) {
            Main.message(sender, "group-notExists", groupName);
            return;
        }

        GroupMember groupMember = group.getGroupMember(player.getUniqueId());

        if (groupMember == null) return;

        if (group.getOwner().equals(groupMember.getUuid())) {
            Main.message(sender, "leave-failOwner");
            return;
        }

        boolean success = Main.getGroupManager().leaveGroup(group.getId(), player.getUniqueId(), true, true);

        if (success) {
            Main.message(sender, "leave-success", groupName);
        } else {
            Main.message(sender, "leave-fail");
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
            if (sender.hasPermission(Permissions.GROUP_LEAVE)) {
                for (Group group : Main.getGroupManager().getMemberGroups(player.getUniqueId())) {
                    if (!group.getOwner().equals(player.getUniqueId()))
                        result.add(group.getName());
                }
            }
        }

        return result;
    }
}
