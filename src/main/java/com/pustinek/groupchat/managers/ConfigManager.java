package com.pustinek.groupchat.managers;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.DatabaseConfig;
import com.pustinek.groupchat.models.RedisConfig;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;


public class ConfigManager extends Manager {
    private final Main plugin;
    private FileConfiguration config;
    public static boolean isDebug = true;
    private String pluginMessagePrefix = "[GroupChat]";

    private DatabaseConfig databaseConfig;
    private RedisConfig redisConfig;


    private String groupChatPrefix = "";
    private String groupChatGenericPrefix = "[gc]";
    private char chatDefaultColor = '7';
    private ChatColor chatColor;


    public ConfigManager(Main plugin) {

        this.plugin = plugin;

        plugin.saveDefaultConfig();

        reloadConfig();
    }

    public void reloadConfig(){
        try{
            plugin.getLogger().info("(re)loading Configs...");
            //Create config file if it doesn't exist
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
        loadSectionRedis(config.getConfigurationSection("redis"));
    }


    private void loadSectionRedis(ConfigurationSection section) {
        if (section == null) {
            //TODO: stop the server or use SQL settings
            Main.error("failed to load Sync configuration !");
            return;
        }

        redisConfig = new RedisConfig(
                section.getBoolean("enabled", false),
                section.getString("ip"),
                section.getInt("port"),
                section.getString("username", ""),
                section.getString("password", ""),
                section.getString("server", "localhost"),
                section.getString("message_channel")
        );

    }

    private void loadSectionSQL(ConfigurationSection section) {

        if (section == null) {
            //TODO: stop the server or use SQL settings
            Main.error("failed to load SQL configuration !");
            return;
        }

        String table_prefix = section.getString("table_prefix");
        if (table_prefix == null || !table_prefix.matches("^([a-zA-Z0-9\\-_]+)?$")) {
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
                section.getInt("ping_interval"),
                section.getInt("max_life_time"));
    }

    private void loadSectionChat(ConfigurationSection section) {
        if (section == null) {
            Main.error("Failed to load chat configs - Missing chat configuration section");
            return;
        }

        groupChatPrefix = section.getString("chat_prefix", "[gc][{group.name}][{user.name}] : ");
        groupChatGenericPrefix = section.getString("generic_msg_prefix", "[gc][{group.name}] : ");
        // Chat color:
        String chatDefaultColorString = section.getString("default_color", "7");
        if (chatDefaultColorString != null)
            chatDefaultColor = chatDefaultColorString.charAt(chatDefaultColorString.length() - 1);
        chatColor = ChatColor.getByChar(chatDefaultColor);
    }

    public String getPluginMessagePrefix() {
        return pluginMessagePrefix;
    }

    public String getGroupChatPrefix() {
        return groupChatPrefix;
    }

    public String getGroupChatGenericPrefix() {
        return groupChatGenericPrefix;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public RedisConfig getRedisConfig() {
        return redisConfig;
    }

}
