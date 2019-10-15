package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import org.bukkit.command.CommandSender;

public class CommandTest extends CommandDefault {

    private final Main plugin;

    public CommandTest(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getCommandStart() {
        return "groupchat test";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.isOp())
            return "help-test";
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.isOp()) return;

        if (args.length < 2) {
            return;
        }

        Main.debug("test command RUN...");

        Main.getRedisManager().publish("groupchat_test", "test string");
    }
}
