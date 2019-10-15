package com.pustinek.groupchat.managers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.listeners.RedisListener;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupInvite;
import redis.clients.jedis.Jedis;

public class RedisManager extends Manager {

    private final Main plugin;

    public RedisManager(Main plugin) {
        this.plugin = plugin;
    }

    public void subscribe() {
        String[] channels = new String[RedisChannels.values().length];

        for (int i = 0; i < RedisChannels.values().length; i++) {
            channels[i] = RedisChannels.values()[i].getValue();
        }

        Thread redisSubThread = new Thread("Redis Subscriber") {
            @Override
            public void run() {
                System.out.println("redis subscribe thread ran.. !!");
                Jedis jedis = Main.getJedisPool().getResource();
                jedis.subscribe(new RedisListener(), channels);

            }
        };

        redisSubThread.start();
    }

    public void publish(String channel, String message) {
        try (Jedis publisher = Main.getJedisPool().getResource()) {
            publisher.publish(channel, message);
        }

    }

    public void removeGroupPublish(Group group) {

        ObjectNode objectNode = Main.mapper.createObjectNode();

        objectNode.put("server", Main.getConfigManager().getRedisConfig().getServer());
        objectNode.put("type", "remove");
        objectNode.put("payload", group.getId().toString());

        Main.getRedisManager().publish(RedisChannels.GROUP.getValue(), objectNode.toString());
    }

    public void updateGroupPublish(Group group) {

        ObjectNode objectNode = Main.mapper.createObjectNode();
        objectNode.put("server", Main.getConfigManager().getRedisConfig().getServer());
        objectNode.put("type", "update");
        objectNode.put("payload", Main.gson.toJson(group));

        Main.getRedisManager().publish(RedisChannels.GROUP.getValue(), objectNode.toString());
    }

    public void addInvitePublish(GroupInvite invite) {
        ObjectNode objectNode = Main.mapper.createObjectNode();
        objectNode.put("server", Main.getConfigManager().getRedisConfig().getServer());
        objectNode.put("type", "create");
        objectNode.put("payload", Main.gson.toJson(invite));

        Main.getRedisManager().publish(RedisChannels.INVITE.getValue(), objectNode.toString());
    }

    public void responseToGroupInvitePublish(GroupInvite invite, Boolean accepted) {
        ObjectNode objectNode = Main.mapper.createObjectNode();
        objectNode.put("server", Main.getConfigManager().getRedisConfig().getServer());
        objectNode.put("type", "respond");
        objectNode.put("payload", Main.gson.toJson(invite));
        objectNode.put("payload_2", accepted);

        Main.getRedisManager().publish(RedisChannels.INVITE.getValue(), objectNode.toString());
    }


    public enum RedisChannels {
        CHAT("GroupChat_chat"),
        GROUP("GroupChat_group"),
        INVITE("GroupChat_invite");

        private String value;

        private RedisChannels(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }
}
