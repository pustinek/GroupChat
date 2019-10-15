package com.pustinek.groupchat.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.managers.RedisManager;
import com.pustinek.groupchat.models.Chat;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupInvite;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;
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
                JsonNode actualObj = Main.mapper.readTree(message);
                JsonNode serverNode = actualObj.get("server");
                JsonNode typeNode = actualObj.get("type");
                JsonNode payloadNode = actualObj.get("payload");

                if (serverNode.textValue().equals(Main.getConfigManager().getRedisConfig().getServer())) return;

                if (typeNode.textValue().equals("update")) {
                    Main.debug("update !");

                    String groupAsString = payloadNode.textValue();

                    Group group = Main.gson.fromJson(groupAsString, Group.class);
                    if (group == null) {
                        Main.error("Failed to parse group on another server !");
                        return;
                    }

                    Main.getGroupManager().updateGroup(group, false);

                } else if (typeNode.textValue().equals("remove")) {
                    Main.debug("delete !!");
                    UUID groupID = UUID.fromString(payloadNode.textValue());
                    Group group = Main.getGroupManager().getGroupClone(groupID);

                    Main.getGroupManager().deleteGroup(group, false, null);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (channel.equals(RedisManager.RedisChannels.INVITE.getValue())) {
            try {
                JsonNode actualObj = Main.mapper.readTree(message);
                JsonNode serverNode = actualObj.get("server");
                JsonNode typeNode = actualObj.get("type");
                JsonNode payloadNode = actualObj.get("payload");

                if (serverNode.textValue().equals(Main.getConfigManager().getRedisConfig().getServer())) return;

                if (typeNode.textValue().equals("create")) {
                    Main.debug("Invite create !");

                    String inviteAsString = payloadNode.textValue();

                    GroupInvite invite = Main.gson.fromJson(inviteAsString, GroupInvite.class);
                    if (invite == null) {
                        Main.error("Failed to parse invite on another server !");
                        return;
                    }

                    Main.getInvitesManager().invitePlayerToGroup(
                            invite.getInviteeID(),
                            invite.getInviterID(),
                            invite.getGroupID(),
                            false,
                            null
                    );
                } else if (typeNode.textValue().equals("respond")) {
                    Main.debug("Invite respond !");
                    String inviteAsString = payloadNode.textValue();
                    JsonNode payload2Node = actualObj.get("payload_2");
                    Boolean inviteAccepted = payload2Node.asBoolean();
                    GroupInvite invite = Main.gson.fromJson(inviteAsString, GroupInvite.class);
                    if (invite == null) {
                        Main.error("Failed to parse invite on another server !");
                        return;
                    }

                    Main.getInvitesManager().respondToGroupInvite(invite, inviteAccepted, false);
                }

            } catch (IOException e) {
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
