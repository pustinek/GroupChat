package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupMember;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandKick extends CommandDefault {

    private final Main plugin;

    public CommandKick(Main plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat kick";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.GROUP_KICK))
            return "help-kick";
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

        if (!sender.hasPermission(Permissions.GROUP_KICK)) {
            Main.message(sender, "kick-noPermission");
            return;
        }

        if (args.length < 3) {
            Main.message(sender, "kick-help");
            return;
        }

        String groupName = args[1];
        String memberName = args[2];

        Group group = Main.getGroupManager().getGroupClone(groupName);
        if (group == null) {
            Main.message(sender, "group-notExists", groupName);
            return;
        }

        GroupMember memberToKick = group.getMembers().stream().filter(member -> member.getUsername().equalsIgnoreCase(memberName)).findAny().orElse(null);

        if (memberToKick == null) {
            Main.message(sender, "player-noExist", memberName);
            return;
        }

        if (!group.getOwner().equals(player.getUniqueId())) {
            Main.message(sender, "group-notTheOwner");
            return;
        }
        if (group.getOwner().equals(memberToKick.getUuid())) {
            Main.message(sender, "kick-ownerFail");
            return;
        }

        boolean success = Main.getGroupManager().kickPlayer(group.getId(), memberToKick.getUuid());

        if (success) {

            Main.message(sender, "kick-success", memberToKick.getUsername(), groupName);
        } else {
            Main.message(sender, "kick-fail", memberToKick.getUsername(), groupName);
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
