package com.pustinek.groupchat.commands;


import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.GroupUtils;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
* Example of implemented command
* */
public class CommandCreate extends CommandDefault {
    private final Main plugin;

    public CommandCreate(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat create";
    }

    @Override
    public String getHelp(CommandSender target) {
        return "help-create";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(player == null) {
            // Command was triggered via the console,
            Main.message(sender, "cmd-onlyByPlayer");
            return;
        }
        if (!sender.hasPermission(Permissions.GROUP_CREATE)) {
            // The player (Sender) doesn't have the required permissions, notify him
            Main.message(sender, "create-noPermission");
            return;
        }


        Integer groupCount = Main.getGroupManager().getOwnerGroups(player.getUniqueId()).size();

        //TODO: create a dynamic permission based group ownership limit !
        if (groupCount > 4) {
            Main.message(sender, "create-maxLimit");
            return;
        }

        if (args.length < 2) {
            Main.message(sender, "create-help");
            return;
        }
        String groupName = args[1];
        Group group = Main.getGroupManager().getGroupByName(groupName);
        if (group != null) {
            Main.message(sender, "group-alreadyExists");
            return;
        }


        if (!GroupUtils.validateGroupName(groupName)) {
            Main.message(sender, "group-invalidName");
            Main.debug(sender.getName() + " tried to create a group with invalid name (" + groupName + ")");
        }

        Main.getGroupManager().createGroup(groupName, player.getUniqueId());

        Main.message(sender, "create-success");


        //TODO: check player limits




    }
}