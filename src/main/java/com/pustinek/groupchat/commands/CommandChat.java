package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChat extends CommandDefault {

    private final Main plugin;

    public CommandChat(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {
        return "groupchat chat";
    }

    @Override
    public String getHelp(CommandSender target) {
        return "help-chat";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;


        plugin.getLogger().info("Chat with me !!");


        if (player == null) {
            // Command was triggered via the console,
            Main.message(sender, "cmd-onlyByPlayer");
            return;
        }


    }
}
