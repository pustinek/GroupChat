package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

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
        if (!sender.hasPermission(Permissions.RELOAD)) {
            Main.message(sender, "reload-noPermission");
            return;
        }


        if (args.length < 2) {
            Main.message(sender, "reload-help");
            return;
        }

        String arg = args[1];

        if (arg.equalsIgnoreCase("config")) {
            Main.getConfigManager().reloadConfig();
            Main.message(sender, "reload-configSuccess");
        } else if (arg.equalsIgnoreCase("redis")) {
            //TODO: reload for redis
            Main.initializeJedisPool();
            if (Main.getRedisConnected())
                Main.message(sender, "reload-redisSuccess");
            else
                Main.message(sender, "reload-redisFail");
        } else {
            Main.message(sender, "reload-help");
        }
    }

    @Override
    public List<String> getTabCompleteList(int toComplete, String[] start, CommandSender sender) {
        List<String> result = new ArrayList<>();
        if (!sender.hasPermission(Permissions.RELOAD)) return result;

        result.add("config");
        result.add("redis");

        return result;
    }

}
