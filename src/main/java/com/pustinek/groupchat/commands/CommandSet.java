package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSet extends CommandDefault {


    private final Main plugin;

    public CommandSet(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat set";
    }

    @Override
    public String getHelp(CommandSender target) {
        return "help-set";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (player == null) {
            // Command was triggered via the console,
            Main.message(sender, "cmd-onlyByPlayer");
            return;
        }
        if (!sender.hasPermission(Permissions.GROUP_SET)) {
            // The player (Sender) doesn't have the required permissions, notify him
            Main.message(sender, "set-noPermission");
            return;
        }

        if (args.length < 2) {
            Main.message(sender, "set-help");
            return;
        }

        String groupName = args[1];


        //TODO: error is thrown if the group doesn't exist !!
        Group group = Main.getGroupManager().getGroupByNameAndMember(groupName, player.getUniqueId());


        if (group == null) {
            Main.message(sender, "group-notExistOrNotOwner");
            return;
        }


        Main.getChatManager().setPlayerActiveGroup(player.getUniqueId(), group.getId());
        Main.message(sender, "set-success");

    }

    @Override
    public List<String> getTabCompleteList(int toComplete, String[] start, CommandSender sender) {
        List<String> result = new ArrayList<>();

        Player player = (Player) sender;

        if (player == null) {
            return result;
        }

        if (toComplete == 2) {
            if (sender.hasPermission(Permissions.GROUP_SET)) {
                for (Group group : Main.getGroupManager().getMemberGroups(player.getUniqueId())) {
                    result.add(group.getName());
                }
            }
        }

        return result;
    }


}
