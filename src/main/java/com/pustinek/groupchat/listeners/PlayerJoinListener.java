package com.pustinek.groupchat.listeners;

import com.pustinek.groupchat.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        Integer i = Main.getInvitesManager().getPlayerGroupInvites(player.getUniqueId()).size();

        if (i > 0) {
            Main.message(player, "invites-pending", i);
        }


    }


}
