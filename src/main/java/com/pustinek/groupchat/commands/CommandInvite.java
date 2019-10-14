package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupInvite;
import com.pustinek.groupchat.utils.Callback;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandInvite extends CommandDefault {

    private final Main plugin;

    public CommandInvite(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getCommandStart() {
        return "groupchat invite";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.GROUP_INVITE))
            return "help-invite";
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
        if (!sender.hasPermission(Permissions.GROUP_INVITE)) {
            // The player (Sender) doesn't have the required permissions, notify him
            Main.message(sender, "You don't have enough permissions to invite players to groups");
            return;
        }

        if (args.length < 3) {
            Main.message(sender, "invite-help");
            return;
        }

        String username = args[2];
        String groupName = args[1];


        Player playerToInvite = Bukkit.getPlayer(username);
        Group group = Main.getGroupManager().getGroupClone(groupName);


        if (playerToInvite == null) {
            Main.message(sender, "player-noExist");
            return;
        }
        if (group == null || !group.getOwner().equals(player.getUniqueId())) {
            Main.message(sender, "group-notExistOrNotOwner");
            return;
        }
        if (group.getMembers().contains(playerToInvite.getUniqueId())) {
            Main.message(sender, "invite-playerMember");
            return;
        }


        Main.getInvitesManager().invitePlayerToGroup(playerToInvite.getUniqueId(), player.getUniqueId(), group.getId(), new Callback<GroupInvite>(plugin) {
            @Override
            public void onResult(GroupInvite result) {

                Main.message(sender, "invite-success");
                Main.message(playerToInvite, "invite-notification", group.getName());
            }

            @Override
            public void onError(Throwable throwable) {
                Main.message(sender, "invite-fail");
                super.onError(throwable);
            }
        });

    }

    @Override
    public List<String> getTabCompleteList(int toComplete, String[] start, CommandSender sender) {
        List<String> result = new ArrayList<>();

        Player player = (Player) sender;

        if (player == null) {
            return result;
        }

        if (toComplete == 2) {
            if (sender.hasPermission(Permissions.GROUP_INVITE)) {
                for (Group group : Main.getGroupManager().getOwnerGroups(player.getUniqueId())) {
                    result.add(group.getName());
                }
            }
        }
        if (toComplete == 3) {
            if (sender.hasPermission(Permissions.GROUP_INVITE)) {
                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                    result.add(onlinePlayers.getName());
                }
            }
        }

        return result;
    }

}
