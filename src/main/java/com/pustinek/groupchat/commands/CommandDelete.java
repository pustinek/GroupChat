package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.DeleteConformation;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandDelete extends CommandDefault {

    private final Main plugin;

    public CommandDelete(Main plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat delete";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.GROUP_DELETE))
            return "help-delete";
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

        if (!sender.hasPermission(Permissions.GROUP_DELETE)) {
            Main.message(sender, "delete-noPermission");
            return;
        }


        if (args.length == 2) {
            String groupName = args[1];
            Group group = Main.getGroupManager().getGroupClone(groupName);


            //TODO: check the validity of this :D
            if (group == null || (!group.getOwner().equals(player.getUniqueId()) && !sender.hasPermission(Permissions.GROUP_ADMIN_DELETE))) {
                Main.message(sender, "group-notExistOrNotOwner");
                return;
            }


            DeleteConformation.deleteConformationHashMap.put(player.getUniqueId(), group);

            Main.message(sender, "delete-confirmation-header", group.getName());


        } else {
            Main.message(sender, "delete-help");
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
            if (sender.hasPermission(Permissions.GROUP_DELETE)) {
                for (Group group : Main.getGroupManager().getMemberGroups(player.getUniqueId())) {
                    result.add(group.getName());
                }
            }
        }

        return result;
    }
}
