package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandList extends CommandDefault {
    private final Main plugin;

    public CommandList(Main plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat list";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.RELOAD))
            return "help-list";
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            Main.message(sender, "cmd-onlyByPlayer");
            return;
        }
        Player player = (Player) sender;

        ArrayList<String> messages = new ArrayList<>();

        if (args.length == 1) {
            List<Group> groups = Main.getGroupManager().getMemberGroups(player.getUniqueId());
            if (groups.isEmpty()) {
                Main.messageNoPrefix(player, "list-noMember");
            } else {
                Main.messageNoPrefix(player, "list-header");
                groups.forEach(group -> Main.messageNoPrefix(sender, "list-group", group.getName()));
            }

        } else if (args.length > 1) {
            List<Group> groups = Main.getGroupManager().getOwnerGroups(player.getUniqueId());
            if (groups.isEmpty()) {
                Main.messageNoPrefix(player, "list-noOwner");
            } else {
                Main.messageNoPrefix(player, "list-header");
                groups.forEach(group -> Main.messageNoPrefix(sender, "list-group", group.getName()));

            }
        }
    }


}
