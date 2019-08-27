package com.pustinek.mctemplate.managers;

import com.pustinek.mctemplate.McTemplate;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager extends Manager {
    private final McTemplate plugin;
    private FileConfiguration config;
    public static boolean debug = false;
    private String pluginMessagePrefix = "[mc-template]";



    //Config variables
    private String configVersion;

    public ConfigManager(McTemplate plugin){

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
            McTemplate.debug(e.getMessage());
        }
    }
    private void loadConfig() {
        configVersion = config.getString("configVersion");
        pluginMessagePrefix = config.getString("pluginMessagePrefix");
    }

    public String getPluginMessagePrefix() {
        return pluginMessagePrefix;
    }
}
