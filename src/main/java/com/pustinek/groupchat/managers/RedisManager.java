package com.pustinek.groupchat.managers;

import com.grack.nanojson.JsonWriter;
import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.listeners.RedisListener;
import com.pustinek.groupchat.models.CachedPlayer;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupInvite;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class RedisManager extends Manager {

    private final Main plugin;
    private static Thread redisSubThread = null;

    public RedisManager(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void shutdown() {

    }

    /*
     * Subscribe to redis channels
     * */
    public void subscribe() {
        if (!Main.getConfigManager().getRedisConfig().isEnabled() || !Main.getRedisConnected()) return;

        String[] channels = new String[RedisChannels.values().length];

        for (int i = 0; i < RedisChannels.values().length; i++) {
            channels[i] = RedisChannels.values()[i].getValue();
        }
        if (redisSubThread == null) {
            redisSubThread = new Thread("Redis Subscriber") {
                @Override
                public void run() {
                    Main.debug("Redis Subscriber thread run...");
                    Jedis jedis = Main.getJedisPool().getResource();
                    jedis.subscribe(new RedisListener(), channels);

                }
            };
            redisSubThread.start();
        }

    }

    /**
     * Publish message to channel
     *
     * @param channel Name of the channel
     * @param message message to send
     */
    public void publish(String channel, String message) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled() || !Main.getRedisConnected()) return;
        try (Jedis publisher = Main.getJedisPool().getResource()) {
            Main.debug("publish trying !");
            publisher.publish(channel, message);
        } catch (Exception ex) {
            Main.error(ex);
        }

    }
    /**
     * Send message when group gets removed
     *
     * @param group  group that was removed
     *
     */
    public void removeGroupPublish(Group group) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled() || !Main.getRedisConnected()) return;


        String json = JsonWriter.string()
                .object()
                .value("server", Main.getConfigManager().getRedisConfig().getServer())
                .value("type", "remove")
                .value("payload", group.getId().toString())
                .end()
                .done();

        Main.getRedisManager().publish(RedisChannels.GROUP.getValue(), json);
    }
    /**
     * Send message when group gets modified
     *
     * @param group  new group / modified group
     *
     */
    public void updateGroupPublish(Group group) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled() || !Main.getRedisConnected()) return;

        String json = JsonWriter.string()
                .object()
                .value("server", Main.getConfigManager().getRedisConfig().getServer())
                .value("type", "update")
                .value("payload", Main.gson.toJson(group))
                .end()
                .done();
        Main.getRedisManager().publish(RedisChannels.GROUP.getValue(), json);
    }
    /**
     * Send message when player is invited to a group
     *
     * @param invite  groupInvite created
     *
     */
    public void addInvitePublish(GroupInvite invite) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled() || !Main.getRedisConnected()) return;


        String json = JsonWriter.string()
                .object()
                .value("server", Main.getConfigManager().getRedisConfig().getServer())
                .value("type", "create")
                .value("payload", Main.gson.toJson(invite))
                .end()
                .done();

        Main.getRedisManager().publish(RedisChannels.INVITE.getValue(), json);
    }
    /**
     * Send message when player responds to invite
     *
     * @param invite groupInvite that was responded to
     * @param accepted was the invite accepted/rejected
     *
     */
    public void responseToGroupInvitePublish(GroupInvite invite, Boolean accepted) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled() || !Main.getRedisConnected()) return;

        String json = JsonWriter.string()
                .object()
                .value("server", Main.getConfigManager().getRedisConfig().getServer())
                .value("type", "respond")
                .value("payload", Main.gson.toJson(invite))
                .value("payload_2", accepted)
                .end()
                .done();

        Main.getRedisManager().publish(RedisChannels.INVITE.getValue(), json);
    }

    /**
     * Save simple player data to cache
     *
     * @param player player to save
     *
     */
    public void savePlayerToCache(Player player) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled() || !Main.getRedisConnected()) return;

        CachedPlayer cachedPlayer = new CachedPlayer(player.getUniqueId(), player.getName());

        String json = Main.gson.toJson(cachedPlayer);
        Jedis jedis = Main.getJedisPool().getResource();
        String key = player.getUniqueId().toString();

        if (!jedis.exists(key)) {
            jedis.set(player.getUniqueId().toString(), json);
            jedis.expire(key, 14400);
            Main.debug("RedisManager::: saving player to cache.");
        }
        jedis.close();
    }

    public CachedPlayer getPlayerFromCache(UUID playerUUID) {
        if (!Main.getConfigManager().getRedisConfig().isEnabled() || !Main.getRedisConnected()) return null;
        Jedis jedis = Main.getJedisPool().getResource();
        if (jedis == null) {
            return null;
        }

        if (jedis.exists(playerUUID.toString())) {
            String json = jedis.get(playerUUID.toString());

            return Main.gson.fromJson(json, CachedPlayer.class);
        }
        return null;
    }



    public enum RedisChannels {
        CHAT("GroupChat_chat"),
        GROUP("GroupChat_group"),
        INVITE("GroupChat_invite");

        private String value;

        RedisChannels(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }
}
