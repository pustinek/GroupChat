package com.pustinek.groupchat.models;

import com.grack.nanojson.*;
import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.enums.GroupMemberRoles;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class Group implements Cloneable {

    private UUID id;
    private UUID owner;
    private String name;
    private String prefix;
    private ChatColor chatColor;
    private ArrayList<GroupMember> memberArrayList = new ArrayList<>();
    private ArrayList<UUID> joinRequests = new ArrayList<>();
    private GroupOptions options;

    // Comming soon
    private int tier = 0; //tiers - member limits


    private int type = 1; // public = 0, private = 1


    public Group(UUID id, UUID owner, String name, String prefix, ArrayList<GroupMember> members) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.prefix = prefix;
        this.memberArrayList = members;
    }

    public Group(UUID id, UUID owner, String name, String options, String members) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        parseGroupOptions(options);
        parseGroupMembers(members);
    }


    public Group(Group org) {
        this.id = org.id;
        this.owner = org.owner;
        this.name = org.name;
        this.memberArrayList = org.memberArrayList;
        this.prefix = org.prefix;
        this.joinRequests = org.joinRequests;
        this.type = org.type;

    }


    private void parseGroupOptions(String options) {
        String[] keyVals = options.split(",");
        for (String keyVal : keyVals) {
            String[] parts = keyVal.split(":", 2);
            if (parts[0].equals("prefix")) {
                this.prefix = parts[1];
            } else if (parts[0].equals("type")) {
            }
        }
    }

    private void parseGroupMembers(String json) {
        if (json == null || json.equals("")) return;
        ArrayList<GroupMember> members = new ArrayList<>();
        try {
            JsonArray membersJsonArray = JsonParser.array().from(json);
            membersJsonArray.forEach((a -> {
                try {
                    JsonObject obj = JsonParser.object().from(a.toString());
                    String username = obj.getString("username");
                    UUID uuid = UUID.fromString(obj.getString("uuid"));
                    GroupMemberRoles role = GroupMemberRoles.valueOf(obj.getString("role"));
                    GroupMember member = new GroupMember(username, uuid, role);
                    members.add(member);
                } catch (JsonParserException e) {
                    e.printStackTrace();
                }
            }));

            this.memberArrayList = members;

        } catch (JsonParserException e) {
            Main.error(e);
            e.printStackTrace();
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

    public ArrayList<UUID> getUUIDOfMembers() {
        return memberArrayList.stream().map(GroupMember::getUuid).collect(Collectors.toCollection(ArrayList::new));
    }


    public GroupMember getOwnerGroupMember() {
        return memberArrayList.stream().filter(member -> member.getRole().equals(GroupMemberRoles.OWNER)).findAny().orElse(null);
    }


    public ArrayList<GroupMember> getMembers() {
        return memberArrayList;
    }

    public GroupMember getGroupMember(UUID memberUUID) {
        return memberArrayList.stream().filter(member -> member.getUuid().equals(memberUUID)).findAny().orElse(null);
    }


    public void addMember(GroupMember member) {
        this.memberArrayList.add(member);
    }

    public void removeMember(UUID playerID) {
        memberArrayList.removeIf(member -> member.getUuid().equals(playerID));
    }

    public int getType() {
        return type;
    }

    public int getTier() {
        return tier;
    }

    /**
     * Transform ArrayList of members to string
     *
     * @return array string of members,
     */

    public String membersToString() {
        ArrayList<String> membersString = new ArrayList<>();
        Main.debug("memberArrayList size -> " + memberArrayList.size() + " :D");

        memberArrayList.forEach(member -> {
            Main.debug("member -> " + member.getUsername() + " :D");
            String json = JsonWriter.string().object()
                    .value("uuid", member.getUuid().toString())
                    .value("username", member.getUsername())
                    .value("role", member.getRole().toString())
                    .end().done();
            membersString.add(json);
        });

        return JsonWriter.string().array(membersString).done();
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

    public enum ManageableOptions {
        PREFIX("prefix"),
        TYPE("type"),
        NAME("name");

        private String value;

        ManageableOptions(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }
}

