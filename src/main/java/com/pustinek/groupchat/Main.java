package com.pustinek.groupchat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pustinek.groupchat.listeners.AsyncPlayerChatListener;
import com.pustinek.groupchat.listeners.PlayerJoinListener;
import com.pustinek.groupchat.managers.*;
import com.pustinek.groupchat.models.RedisConfig;
import com.pustinek.groupchat.sql.Database;
import com.pustinek.groupchat.sql.MySQL;
import com.pustinek.groupchat.sql.SQLite;
import com.pustinek.groupchat.utils.Callback;
import fr.minuskube.inv.InventoryManager;
import me.wiefferink.interactivemessenger.processing.Message;
import me.wiefferink.interactivemessenger.source.LanguageManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

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
    private static InvitesManager invitesManager = null;

    private static ChatManager chatManager = null;
    private static GUIManager guiManager = null;
    private static InventoryManager inventoryManager = null;
    public static Gson gson = null;
    public static ObjectMapper mapper = null;
    private static RedisManager redisManager = null;





    private static Database database;
    private static JedisPool jedisPool;
    private static Boolean redisConnected = false;
    // General variables:



    public static Database getDatabase() {
        return database;
    }
    public static RedisManager getRedisManager() {
        return redisManager;
    }





    /**
     * Print an error to the console.
     * @param message The message to print
     */
    public static void error(Object... message) {
        logger.severe(StringUtils.join(message, " "));
    }

    public static GroupManager getGroupManager() {
        return groupManager;
    }

    public static InvitesManager getInvitesManager() {
        return invitesManager;
    }

    public static ChatManager getChatManager() {
        return chatManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static GUIManager getGuiManager() {
        return guiManager;
    }

    public static InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public static JedisPool getJedisPool() {
        return jedisPool;
    }

    public static Boolean getRedisConnected() {
        return redisConnected;
    }

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

    /**
     * Print a warning to the console.
     *
     * @param message The message to print
     */
    public static void warrning(String message) {
        logger.warning(message);
    }

    /**
     * Print a debug msg to the console.
     *
     * @param message The message to print
     */
    public static void debug(String message) {
        if (ConfigManager.isDebug)
            logger.info(message);
    }

    @Override
    public void onDisable() {

        Bukkit.getServer().getScheduler().cancelTasks(this);
        // Plugin shutdown logic
        for (Manager manager : managers) {
            manager.shutdown();
            manager = null;
        }

        if (database != null) {
            database.disconnect();
        }

        HandlerList.unregisterAll(this);

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
        initializeJedisPool();


        redisManager.subscribe();


        gson = new Gson();
        mapper = new ObjectMapper();

        LanguageManager languageManager = new LanguageManager(
                this,                                  // The plugin (used to get the languages bundled in the jar file)
                "languages",                           // Folder where the languages are stored
                getConfig().getString("language"),     // The language to use indicated by the plugin user
                "EN",                                  // The default language, expected to be shipped with the plugin and should be complete, fills in gaps in the user-selected language
                Collections.singletonList(configManager.getPluginMessagePrefix()) // Chat prefix to use with Message#prefix(), could of course come from the config file
        );
    }

    private void loadManagers() {
        configManager = new ConfigManager(this);
        managers.add(configManager);
        commandManager = new CommandManager(this);
        managers.add(commandManager);
        groupManager = new GroupManager(this);
        managers.add(groupManager);
        invitesManager = new InvitesManager(this);
        managers.add(invitesManager);
        chatManager = new ChatManager(this);
        managers.add(chatManager);
        guiManager = new GUIManager(this);
        managers.add(guiManager);
        redisManager = new RedisManager(this);
        managers.add(redisManager);


        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

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

        invitesManager.reloadInvites(new Callback<Integer>(this) {
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

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AsyncPlayerChatListener(this), this);
        pm.registerEvents(new PlayerJoinListener(), this);
    }

    private void initializeJedisPool() {
        RedisConfig redisConfig = configManager.getRedisConfig();
        jedisPool = new JedisPool(
                new JedisPoolConfig(), redisConfig.getIp(), redisConfig.getPort()
        );
        try {
            Jedis jedis = jedisPool.getResource();

            redisConnected = true;
        } catch (JedisConnectionException ex) {
            redisConnected = false;
        }


    }

}
