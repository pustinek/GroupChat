package com.pustinek.groupchat.models;

import com.pustinek.groupchat.Main;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.UUID;

public class Group implements Cloneable {

    private UUID id;
    private UUID owner;
    private String name;
    private String prefix;
    private ChatColor chatColor;
    private ArrayList<UUID> members;
    private ArrayList<UUID> joinRequests;
    private GroupOptions options;

    // Comming soon
    private int tier = 0; //tiers - member limits


    private int type = 1; // public = 0, private = 1


    public Group(UUID id, UUID owner, String name, String prefix, ArrayList<UUID> members) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.prefix = prefix;
        this.members = members;
    }

    public Group(UUID id, UUID owner, String name, ArrayList<UUID> members, String options) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.members = members;
        parseGroupOptions(options);
    }


    public Group(Group org) {
        this.id = org.id;
        this.owner = org.owner;
        this.name = org.name;
        this.members = org.members;
        this.prefix = org.prefix;
        this.joinRequests = org.joinRequests;
        this.type = org.type;

    }


    private void parseGroupOptions(String options) {
        Main.debug("options ready to be parse --> " + options);
        String[] keyVals = options.split(",");
        for (String keyVal : keyVals) {
            String[] parts = keyVal.split(":", 2);
            Main.debug("[0]: " + parts[0] + "...[1]: " + parts[1]);
            if (parts[0].equals("prefix")) {
                this.prefix = parts[1];
            } else if (parts[0].equals("type")) {
                Main.debug("type found with value" + parts[1]);
            }
        }


    }


    public UUID getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public ArrayList<UUID> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<UUID> members) {
        this.members = members;
    }

    public int getType() {
        return type;
    }

    /**
     * Transform ArrayList of UUIDs to string
     *
     * @return string of members separated by ,
     */
    public String getMembersAsString() {
        StringBuilder sb = new StringBuilder();
        for (UUID uuid : members) {
            sb.append(uuid);
            sb.append(",");
        }
        return sb.toString();
    }

    /**
     * Transform all options to string
     *
     * @return string of options separated by ,
     */
    public String getOptionsAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("prefix:").append(prefix).append(",");
        sb.append("chatColor:").append(chatColor).append(",");
        sb.append("type:").append(type).append(",");
        sb.append("tier:").append(tier);
        return sb.toString();
    }

    public enum ManagableOptions {
        PREFIX("prefix"),
        TYPE("type"),
        NAME("name");

        private String value;

        private ManagableOptions(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }


}
