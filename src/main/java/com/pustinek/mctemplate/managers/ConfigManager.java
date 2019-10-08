package com.pustinek.mctemplate.managers;

import com.pustinek.mctemplate.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager extends Manager {
    private final Main plugin;
    private FileConfiguration config;
    public static boolean isDebug = true;
    private String pluginMessagePrefix = "[mc-template]";



    //Config variables
    private String configVersion;

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
        configVersion = config.getString("configVersion");
        isDebug = config.getBoolean("debug", false);
        pluginMessagePrefix = config.getString("pluginMessagePrefix");
    }

    public String getPluginMessagePrefix() {
        return pluginMessagePrefix;
    }
}
