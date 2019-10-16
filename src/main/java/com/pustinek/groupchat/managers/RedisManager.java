package com.pustinek.groupchat.managers;

import com.grack.nanojson.JsonWriter;
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
        if (!Main.getConfigManager().getRedisConfig().isEnabled()) return;

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
        Main.debug("publish called !");
        try (Jedis publisher = Main.getJedisPool().getResource()) {
            Main.debug("publish trying !");
            publisher.publish(channel, message);
        } catch (Exception ex) {
            Main.error(ex);
        }

    }

    public void removeGroupPublish(Group group) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled()) return;


        String json = JsonWriter.string()
                .object()
                .value("server", Main.getConfigManager().getRedisConfig().getServer())
                .value("type", "remove")
                .value("payload", group.getId().toString())
                .end()
                .done();



        /*ObjectNode objectNode = Main.mapper.createObjectNode();

        objectNode.put("server", Main.getConfigManager().getRedisConfig().getServer());
        objectNode.put("type", "remove");
        objectNode.put("payload", group.getId().toString());
        */
        Main.getRedisManager().publish(RedisChannels.GROUP.getValue(), json);
    }

    public void updateGroupPublish(Group group) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled()) return;

        //ObjectNode objectNode = Main.mapper.createObjectNode();

        String json = JsonWriter.string()
                .object()
                .value("server", Main.getConfigManager().getRedisConfig().getServer())
                .value("type", "update")
                .value("payload", Main.gson.toJson(group))
                .end()
                .done();


        /*objectNode.put("server", Main.getConfigManager().getRedisConfig().getServer());
        objectNode.put("type", "update");
        objectNode.put("payload", Main.gson.toJson(group));*/

        Main.getRedisManager().publish(RedisChannels.GROUP.getValue(), json);
    }

    public void addInvitePublish(GroupInvite invite) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled()) return;


        String json = JsonWriter.string()
                .object()
                .value("server", Main.getConfigManager().getRedisConfig().getServer())
                .value("type", "create")
                .value("payload", Main.gson.toJson(invite))
                .end()
                .done();

      /*  ObjectNode objectNode = Main.mapper.createObjectNode();
        objectNode.put("server", Main.getConfigManager().getRedisConfig().getServer());
        objectNode.put("type", "create");
        objectNode.put("payload", Main.gson.toJson(invite));*/

        Main.getRedisManager().publish(RedisChannels.INVITE.getValue(), json);
    }

    public void responseToGroupInvitePublish(GroupInvite invite, Boolean accepted) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled()) return;


        String json = JsonWriter.string()
                .object()
                .value("server", Main.getConfigManager().getRedisConfig().getServer())
                .value("type", "respond")
                .value("payload", Main.gson.toJson(invite))
                .value("payload_2", accepted)
                .end()
                .done();

      /*  ObjectNode objectNode = Main.mapper.createObjectNode();
        objectNode.put("server", Main.getConfigManager().getRedisConfig().getServer());
        objectNode.put("type", "respond");
        objectNode.put("payload", Main.gson.toJson(invite));
        objectNode.put("payload_2", accepted);*/

        Main.getRedisManager().publish(RedisChannels.INVITE.getValue(), json);
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
