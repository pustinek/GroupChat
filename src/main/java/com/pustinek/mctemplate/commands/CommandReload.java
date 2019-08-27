package com.pustinek.mctemplate.commands;

import com.pustinek.mctemplate.McTemplate;
import com.pustinek.mctemplate.utils.Permissions;
import org.bukkit.command.CommandSender;

public class CommandReload extends CommandDefault {
    private final McTemplate plugin;

    public CommandReload(McTemplate plugin) {

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
            McTemplate.getConfigManager().reloadConfig();

    }
}
