package com.pustinek.groupchat.commands;


import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.GroupUtils;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandManage extends CommandDefault {

    private final Main plugin;

    public CommandManage(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getCommandStart() {
        return "groupchat manage";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.GROUP_MANAGE))
            return "help-manage";
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

        if (!sender.hasPermission(Permissions.GROUP_MANAGE)) {
            Main.message(sender, "manage-noPermission");
            return;
        }

        if (args.length < 4) {
            Main.message(sender, "manage-help");
            return;
        }

        String groupName = args[1];
        String variable = args[2];
        String variableValue = args[3];
        Group group = Main.getGroupManager().getGroupClone(groupName);

        if (group == null || !group.getOwner().equals(player.getUniqueId()) && !sender.hasPermission(Permissions.GROUP_ADMIN_MANAGE)) {
            Main.message(sender, "group-notExistOrNotOwner");
            return;
        }


        if (variable.equalsIgnoreCase("prefix")) {
            if (!GroupUtils.validateGroupPrefix(variableValue)) {
                Main.message(sender, "manage-invalidPrefix");
                return;
            }
            Main.getGroupManager().changeGroupPrefix(group.getId(), variableValue);
            Main.message(sender, "manage-prefixChangeSuccess", groupName, variableValue);

        } else if (variable.equalsIgnoreCase("type")) {
            Main.message(sender, "feature-inTheWork");
        } else if (variable.equalsIgnoreCase("name")) {
            Main.message(sender, "feature-inTheWork");
        }

    }

    @Override
    public List<String> getTabCompleteList(int toComplete, String[] start, CommandSender sender) {
        List<String> result = new ArrayList<>();

        Player player = (Player) sender;

        if (player == null || !sender.hasPermission(Permissions.GROUP_MANAGE)) {
            return result;
        }

        if (toComplete == 2) {
            for (Group group : Main.getGroupManager().getOwnerGroups(player.getUniqueId())) {
                result.add(group.getName());
            }
        } else if (toComplete == 3) {
            for (Group.ManageableOptions option : Group.ManageableOptions.values()) {
                result.add(option.getValue());
            }
        }

        return result;
    }


}


