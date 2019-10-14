package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.managers.GUIManager;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInvites extends CommandDefault {

    private final Main plugin;

    public CommandInvites(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat invites";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.GROUP_INVITES))
            return "help-invites";
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            Main.message(sender, "cmd-onlyByPlayer");
            return;
        }
        Player player = (Player) sender;

        if (!player.hasPermission(Permissions.GROUP_INVITES)) {
            Main.message(player, "invites-noPermission");
            return;
        }

        GUIManager.getInvitesGUI().open(player);

    }
}
