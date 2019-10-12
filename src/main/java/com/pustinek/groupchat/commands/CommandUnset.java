package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnset extends CommandDefault {
    private final Main plugin;

    public CommandUnset(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat unset";
    }

    @Override
    public String getHelp(CommandSender target) {
        return "help-unset";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (player == null) {
            // Command was triggered via the console,
            Main.message(sender, "cmd-onlyByPlayer");
            return;
        }
        if (!sender.hasPermission(Permissions.GROUP_UNSET)) {
            // The player (Sender) doesn't have the required permissions, notify him
            Main.message(sender, "unset-noPermission");
            return;
        }

        Main.getChatManager().unsetPlayerActiveGroup(player.getUniqueId());

    }


}


