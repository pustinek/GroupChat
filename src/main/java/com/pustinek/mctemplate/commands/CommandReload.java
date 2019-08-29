package com.pustinek.mctemplate.commands;

import com.pustinek.mctemplate.Main;
import com.pustinek.mctemplate.utils.Permissions;
import org.bukkit.command.CommandSender;

public class CommandReload extends CommandDefault {
    private final Main plugin;

    public CommandReload(Main plugin) {

        this.plugin = plugin;
    }

    //TODO [CHANGE_ME] - change command prefix/plugin name
    @Override
    public String getCommandStart() {
        return "mctemplate reload";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.RELOAD))
            return "/reload - reload configs";
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(Permissions.RELOAD))
            Main.getConfigManager().reloadConfig();

    }
}
