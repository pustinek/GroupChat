package com.pustinek.groupchat.utils;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.managers.Manager;
import com.pustinek.groupchat.models.Group;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class DeleteConformation extends Manager {
    public static final HashMap<UUID, Group> deleteConformationHashMap = new HashMap<>();


    private final Main plugin;

    public DeleteConformation(Main plugin) {
        this.plugin = plugin;
    }


    public void validate(String message, Player player) {
        Group group = deleteConformationHashMap.get(player.getUniqueId());
        if (group == null) return;
        if (message.equalsIgnoreCase("cancel")) {
            Main.message(player, "delete-confirmation-cancel");
            deleteConformationHashMap.remove(player.getUniqueId());
            return;
        }
        if (group.getName().equals(message)) {
            //Delete message
            Main.getGroupManager().deleteGroup(group, true, new Callback<UUID>(plugin) {
                @Override
                public void onResult(UUID result) {
                    deleteConformationHashMap.remove(player.getUniqueId());
                    Main.message(player, "delete-success");
                }

                @Override
                public void onError(Throwable throwable) {
                    Main.message(player, "delete-fail");
                    deleteConformationHashMap.remove(player.getUniqueId());
                    Main.error(throwable);
                }
            });


        } else {
            Main.message(player, "delete-confirmation-fail");
            deleteConformationHashMap.remove(player.getUniqueId());
        }
    }

}
