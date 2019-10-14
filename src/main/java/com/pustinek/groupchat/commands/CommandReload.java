package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;

public class CommandReload extends CommandDefault {
    private final Main plugin;

    public CommandReload(Main plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat reload";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.RELOAD))
            return "help-reload";
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permissions.RELOAD)) return;
            Main.getConfigManager().reloadConfig();
        Main.message(sender, "reload-success");
    }
}
