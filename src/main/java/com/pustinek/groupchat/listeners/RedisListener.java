package com.pustinek.groupchat.listeners;

import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;
import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.managers.RedisManager;
import com.pustinek.groupchat.models.Chat;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupInvite;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisListener extends JedisPubSub {
    @Override
    public void onMessage(String channel, String message) {
        Main.debug("[RCT] " + channel + " --> " + message);

        if (channel.equals(RedisManager.RedisChannels.CHAT.getValue())) {

            Chat chatObj = Main.gson.fromJson(message, Chat.class);
            if (chatObj == null) return;
            if (chatObj.getServer().equals(Main.getConfigManager().getRedisConfig().getServer())) return;


            Main.debug(chatObj.toString());

            Main.getChatManager().sendChatToGroupMembers(chatObj);

        }


        if (channel.equals(RedisManager.RedisChannels.GROUP.getValue())) {
            try {


                JsonObject object = JsonParser.object().from(message);

                String server = object.getString("server");
                String type = object.getString("type");
                String payload = object.getString("payload");


                if (server.equals(Main.getConfigManager().getRedisConfig().getServer())) return;

                if (type.equals("update")) {
                    Group group = Main.gson.fromJson(payload, Group.class);
                    if (group == null) {
                        Main.error("Failed to parse group on another server !");
                        return;
                    }

                    Main.getGroupManager().updateGroup(group, false, false);

                } else if (type.equals("remove")) {
                    UUID groupID = UUID.fromString(payload);
                    Group group = Main.getGroupManager().getGroupClone(groupID);
                    Main.getGroupManager().deleteGroup(group, false, null);
                }

            } catch (JsonParserException e) {
                e.printStackTrace();
            }
        } else if (channel.equals(RedisManager.RedisChannels.INVITE.getValue())) {
            try {

                JsonObject object = JsonParser.object().from(message);

                String server = object.getString("server");
                String type = object.getString("type");
                String payload = object.getString("payload");

                if (server.equals(Main.getConfigManager().getRedisConfig().getServer())) return;

                if (type.equals("create")) {

                    GroupInvite invite = Main.gson.fromJson(payload, GroupInvite.class);
                    if (invite == null) {
                        Main.error("Failed to parse invite on another server !");
                        return;
                    }

                    Main.getInvitesManager().invitePlayerToGroup(
                            invite.getInviteeID(),
                            invite.getInviteeUsername(),
                            invite.getInviterID(),
                            invite.getGroupID(),
                            false,
                            false,
                            null
                    );
                } else if (type.equals("respond")) {

                    Boolean inviteAcceptedPayload = object.getBoolean("payload_2");
                    GroupInvite invite = Main.gson.fromJson(payload, GroupInvite.class);
                    if (invite == null) {
                        Main.error("Failed to parse invite on another server !");
                        return;
                    }
                    Main.getInvitesManager().respondToGroupInvite(invite, inviteAcceptedPayload, false, false);
                }

            } catch (JsonParserException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        Main.debug("Redis subscribed to channel - " + channel);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        Main.debug("Redis unSubscribed to channel - " + channel);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
    }
}
