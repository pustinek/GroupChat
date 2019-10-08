package com.pustinek.mctemplate;

import com.pustinek.mctemplate.listeners.ExampleListener;
import com.pustinek.mctemplate.managers.CommandManager;
import com.pustinek.mctemplate.managers.ConfigManager;
import com.pustinek.mctemplate.managers.Manager;
import com.pustinek.mctemplate.utils.ChatUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

//TODO [CHANGE_ME] - plugin name + file name
public final class Main extends JavaPlugin {
    // Private static variables
    private static Logger logger;

    // Managers:
    private Set<Manager> managers = new HashSet<>();
    private static CommandManager commandManager = null;
    private static ConfigManager configManager = null;
    // General variables:


    @Override
    public void onEnable() {
        // load logger
        logger = this.getLogger();
        // Plugin startup logic

        //load managers
        loadManagers();
        registerListeners();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Print a warning to the console.
     * @param message The message to print
     */
    public static void warrning(String message) {
        logger.warning(message);
    }

    /**
     * Print a debug msg to the console.
     * @param message The message to print
     */
    public static void debug(String message) {
        if (ConfigManager.isDebug)
            logger.info(message);
    }

    /**
     * Send message to sender, without plugin prefix
     *
     * @param sender  the one to which send the message
     * @param message message to be sent
     */
    public static void messageNoPrefix(CommandSender sender, String message) {
        sender.sendMessage(ChatUtils.chatColor(message));
    }

    /**
     * Send message to sender with plugin prefix
     *
     * @param sender  the one to which send the message
     * @param message message to be sent
     */
    public static void message(CommandSender sender, String message) {
        sender.sendMessage(ChatUtils.chatColor(configManager.getPluginMessagePrefix()) + ChatUtils.chatColor(message));
    }

    /**
     * Print an error to the console.
     * @param message The message to print
     */
    public static void error(Object... message) {
        logger.severe(StringUtils.join(message, " "));
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ExampleListener(this), this);

    }

    private void loadManagers() {
        configManager = new ConfigManager(this);
        managers.add(configManager);
        commandManager = new CommandManager(this);
        managers.add(commandManager);

    }

}
