package com.pustinek.groupchat.managers;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.DatabaseConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;


public class ConfigManager extends Manager {
    private final Main plugin;
    private FileConfiguration config;
    public static boolean isDebug = true;
    private String pluginMessagePrefix = "[GroupChat]";

    private DatabaseConfig databaseConfig;

    private String chatPrefix = "";
    private String groupFormatPrefix = "";
    private String defaultChatColor = "&f";




    public ConfigManager(Main plugin) {

        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig(){
        try{
            plugin.getLogger().info("reloading Configs...");
            //Create config file if it doesn't exist
            plugin.saveDefaultConfig();
            //Reload config
            plugin.reloadConfig();
            config = plugin.getConfig();
            //Start reading from config file
            loadConfig();
        }catch (Exception e){
            Main.debug(e.getMessage());
        }
    }
    private void loadConfig() {
        isDebug = config.getBoolean("debug", false);
        plugin.getLogger().info("debug is " + isDebug);
        pluginMessagePrefix = config.getString("plugin_message_prefix");

        loadSectionSQL(config.getConfigurationSection("sql"));
        loadSectionChat(config.getConfigurationSection("chat"));
    }

    private void loadSectionSQL(ConfigurationSection section) {

        if (section == null) {
            //TODO: stop the server or use SQL settings
            Main.error("failed to load SQL configuration !");
            return;
        }

        String table_prefix = section.getString("table_prefix");
        if (table_prefix == null || !table_prefix.matches("^([a-zA-Z0-9\\-\\_]+)?$")) {
            Main.error("Database table prefix contains illegal letters or is missing, using 'groupchat_' prefix.");
            table_prefix = "groupchat_";
        }

        databaseConfig = new DatabaseConfig(
                section.getString("driver"),
                section.getString("address"),
                section.getString("database"),
                section.getString("username"),
                section.getString("password"),
                table_prefix,
                section.getString("options"),
                section.getInt("ping_interval"));

    }

    private void loadSectionChat(ConfigurationSection section) {
        if (section == null) {
            Main.error("Failed to load chat configs - Missing chat configuration section");
            return;
        }

        chatPrefix = section.getString("prefix");
        groupFormatPrefix = section.getString("group_prefix", "[{group_prefix}]");
        defaultChatColor = section.getString("default_color", "&7");

    }

    public String getPluginMessagePrefix() {
        return pluginMessagePrefix;
    }

    public String getChatPrefix() {
        return chatPrefix;
    }

    public String getGroupFormatPrefix() {
        return groupFormatPrefix;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public String getDefaultChatColor() {
        return defaultChatColor;
    }
}
