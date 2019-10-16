package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if (target.hasPermission(Permissions.GROUP_LIST))
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
            List<Group> memberGroups = Main.getGroupManager().getMemberGroups(player.getUniqueId());
            List<Group> ownerGroups = Main.getGroupManager().getOwnerGroups(player.getUniqueId());

            List<Group> filteredMembersList = memberGroups.stream().filter(group -> !group.getOwner().equals(player.getUniqueId())).collect(Collectors.toList());

            if (memberGroups.isEmpty() && ownerGroups.isEmpty()) {
                Main.messageNoPrefix(player, "list-noMember");
            } else {
                Main.messageNoPrefix(player, "list-header");
                ownerGroups.forEach(group -> Main.messageNoPrefix(sender, "list-groupOwner", group.getName()));
                filteredMembersList.forEach(group -> Main.messageNoPrefix(sender, "list-groupMember", group.getName()));
                Main.messageNoPrefix(player, "list-footer");
            }


        }
    }


}
