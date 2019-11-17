package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.utils.Permissions;
import org.bukkit.command.CommandSender;


public class CommandStatus extends CommandDefault {

    private final Main plugin;

    public CommandStatus(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getCommandStart() {

        return "groupchat status";
    }

    @Override
    public String getHelp(CommandSender target) {
        if (target.hasPermission(Permissions.PLUGIN_STATUS))
            return "help-status";
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!sender.hasPermission(Permissions.PLUGIN_STATUS)) {
            // The player (Sender) doesn't have the required permissions, notify him
            Main.message(sender, "status-noPermission");
            return;
        }

        Main.message(sender, "status-pluginVersion", plugin.getDescription().getVersion());
        Main.message(sender, "status-databaseType", Main.getConfigManager().getDatabaseConfig().getDriver());

        String redisStatusConnection = Main.getRedisConnected() ? "connected" : "disconnected";
        String redisStatusEnabled = Main.getConfigManager().getRedisConfig().isEnabled() ? "Enabled" : "Disabled";
        Main.message(sender, "status-redis", redisStatusEnabled, redisStatusConnection);

    }
}
