package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.managers.GUIManager;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInfo extends CommandDefault {
    private final Main plugin;

    public CommandInfo(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat info";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.GROUP_INFO))
            return "help-info";
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            Main.message(sender, "cmd-onlyByPlayer");
            return;
        }

        Player player = (Player) sender;


        if (args.length != 2) {
            Main.message(player, "info-help");
            return;
        }

        String groupName = args[1];

        Group group = Main.getGroupManager().getGroupClone(groupName);

        if (group == null) {
            Main.message(sender, "group-notExistOrNotMember");
            return;
        }

        GUIManager.getMainGUI(group).open(player);


    }
}
