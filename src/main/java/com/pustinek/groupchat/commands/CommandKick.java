package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

        Group group = Main.getGroupManager().getGroup(groupName);
        if (group == null) {
            Main.message(sender, "group-notExists", groupName);
            return;
        }

        ArrayList<OfflinePlayer> groupMembers = new ArrayList<>();
        group.getMembers().forEach(member -> {
            groupMembers.add(Bukkit.getOfflinePlayer(member));
        });

        Player playerToKick = null;
        for (OfflinePlayer myPlayer : groupMembers) {
            if (myPlayer.getName().equals(memberName)) {
                playerToKick = myPlayer.getPlayer();
                break;
            }
        }


        if (playerToKick == null) {
            Main.message(sender, "player-noExist", memberName);
            return;
        }

        if (!group.getOwner().equals(player.getUniqueId())) {
            Main.message(sender, "group-notTheOwner");
            return;
        }
        if (group.getOwner().equals(playerToKick.getUniqueId())) {
            Main.message(sender, "kick-ownerFail");
            return;
        }

        Main.getGroupManager().kickPlayer(group.getId(), playerToKick.getUniqueId());

        Main.message(sender, "kick-success", playerToKick, groupName);

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
