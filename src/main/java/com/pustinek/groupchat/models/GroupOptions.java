package com.pustinek.groupchat.models;

import org.bukkit.ChatColor;

public class GroupOptions {

    String prefix;
    ChatColor chatColor;
    int type;
    int tier;


    public GroupOptions(String prefix, ChatColor chatColor, int type, int tier) {
        this.prefix = prefix;
        this.chatColor = chatColor;
        this.type = type;
        this.tier = tier;
    }

    public GroupOptions(String prefix, ChatColor chatColor) {
        this.prefix = prefix;
        this.chatColor = chatColor;
        this.type = 0;
        this.tier = 0;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public void setChatColor(ChatColor chatColor) {
        this.chatColor = chatColor;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("prefix:").append(prefix).append(",");
        sb.append("chatColor:").append(chatColor).append(",");
        sb.append("type:").append(type).append(",");
        sb.append("tier:").append(tier);
        return sb.toString();
    }


}
