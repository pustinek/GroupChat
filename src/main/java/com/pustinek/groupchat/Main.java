package com.pustinek.groupchat;

import com.pustinek.groupchat.listeners.AsyncPlayerChatListener;
import com.pustinek.groupchat.managers.*;
import com.pustinek.groupchat.sql.Database;
import com.pustinek.groupchat.sql.MySQL;
import com.pustinek.groupchat.sql.SQLite;
import com.pustinek.groupchat.utils.Callback;
import me.wiefferink.interactivemessenger.processing.Message;
import me.wiefferink.interactivemessenger.source.LanguageManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    // Private static variables
    private static Logger logger;

    // Managers:
    private Set<Manager> managers = new HashSet<>();
    private static CommandManager commandManager = null;
    private static ConfigManager configManager = null;
    private static GroupManager groupManager = null;
    private static ChatManager chatManager = null;

    private static Database database;

    // General variables:

    /**
     * Print a debug stack to the console.
     *
     * @param throwable The message to print
     */
    public static void debug(Throwable throwable) {
        if (ConfigManager.isDebug) {
            logger.info("" + throwable);
        }
    }

    /**
     * Send a message to a target without a prefix.
     *
     * @param target       The target to send the message to
     * @param key          The key of the language string
     * @param replacements The replacements to insert in the message
     */
    public static void messageNoPrefix(Object target, String key, Object... replacements) {
        Message.fromKey(key).replacements(replacements).send(target);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Send a message to a target, prefixed by the default chat prefix.
     *
     * @param target       The target to send the message to
     * @param key          The key of the language string
     * @param replacements The replacements to insert in the message
     */
    public static void message(Object target, String key, Object... replacements) {
        Message.fromKey(key).prefix().replacements(replacements).send(target);
    }

    public static GroupManager getGroupManager() {
        return groupManager;
    }

    public static ChatManager getChatManager() {
        return chatManager;
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

    public static Database getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        // load logger
        logger = this.getLogger();
        // Plugin startup logic
        logger.info(" onEnable");
        //load managers
        loadManagers();
        registerListeners();
        initDatabase();
        initializeGroups();

        LanguageManager languageManager = new LanguageManager(
                this,                                  // The plugin (used to get the languages bundled in the jar file)
                "languages",                           // Folder where the languages are stored
                getConfig().getString("language"),     // The language to use indicated by the plugin user
                "EN",                                  // The default language, expected to be shipped with the plugin and should be complete, fills in gaps in the user-selected language
                Collections.singletonList(configManager.getPluginMessagePrefix()) // Chat prefix to use with Message#prefix(), could of course come from the config file
        );
    }

    private void initDatabase() {
        logger.info("Init database");
        if (configManager.getDatabaseConfig().getDriver().equals(Database.DatabaseType.MySQL.getValue())) {
            logger.info("Using database type: MySQL");
            database = new MySQL(this);
            if (configManager.getDatabaseConfig().getPing_interval() > 0) {
                Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                    @Override
                    public void run() {
                        if (database instanceof MySQL) {
                            ((MySQL) database).ping();
                        }
                    }
                }, configManager.getDatabaseConfig().getPing_interval() * 20L, configManager.getDatabaseConfig().getPing_interval() * 20L);
            }
        } else {
            logger.info("Using database type: SQLite");
            database = new SQLite(this);
        }
    }


/*
    */
/**
     * Send message to sender, without plugin prefix
     *
     * @param sender  the one to which send the message
     * @param message message to be sent
     *//*

    public static void messageNoPrefix(CommandSender sender, String message) {
        sender.sendMessage(ChatUtils.chatColor(message));
    }

    */
/**
     * Send message to sender with plugin prefix
     *
     * @param sender  the one to which send the message
     * @param message message to be sent
     *//*

    public static void message(CommandSender sender, String message) {
        sender.sendMessage(ChatUtils.chatColor(configManager.getPluginMessagePrefix()) + ChatUtils.chatColor(message));
    }
*/

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
        pm.registerEvents(new AsyncPlayerChatListener(this), this);

    }

    private void loadManagers() {
        configManager = new ConfigManager(this);
        managers.add(configManager);
        commandManager = new CommandManager(this);
        managers.add(commandManager);
        groupManager = new GroupManager(this);
        managers.add(groupManager);
        chatManager = new ChatManager(this);
        managers.add(chatManager);
    }

    private void initializeGroups() {
        debug("Initializing Groups...");

        groupManager.reloadGroups(new Callback<Integer>(this) {
            @Override
            public void onResult(Integer result) {
                debug("Initialized " + result + " Groups");
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable != null) error(throwable.getMessage());
                getServer().getPluginManager().disablePlugin(Main.this);
            }
        });

        groupManager.reloadInvites(new Callback<Integer>(this) {
            @Override
            public void onResult(Integer result) {
                debug("Initialized " + result + " group invites");
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable != null) error(throwable.getMessage());
                getServer().getPluginManager().disablePlugin(Main.this);
            }
        });

    }

}
