package com.pustinek.groupchat.gui;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.managers.GUIManager;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.models.GroupInvite;
import com.pustinek.groupchat.utils.GUIItemGenerator;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class GroupInvites implements InventoryProvider {

    private final Group group;

    public GroupInvites(Group group) {
        this.group = group;
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        List<GroupInvite> playerInvites = Main.getInvitesManager().getGroupPlayerInvites(player.getUniqueId());


        ClickableItem[] items = new ClickableItem[playerInvites.size()];

        for (int i = 0; i < playerInvites.size(); i++) {
            GroupInvite gi = playerInvites.get(i);
            Player invitedPlayer = (Player) Bukkit.getOfflinePlayer(gi.getInviteeID());

            items[i] = ClickableItem.empty(
                    GUIItemGenerator.itemGenerator(invitedPlayer.getName(), Material.WHITE_WOOL, Collections.singletonList(""))
            );
        }


        ClickableItem navBorder = ClickableItem.empty(
                GUIItemGenerator.itemGenerator("", Material.GRAY_STAINED_GLASS_PANE, null)
        );

        pagination.setItems(items);
        pagination.setItemsPerPage(36);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
        contents.fillRow(4, navBorder);
        contents.set(4, 0, ClickableItem.of(
                GUIItemGenerator.itemGenerator("&5Main Menu", Material.CRAFTING_TABLE, Collections.singletonList(""))
                ,
                e -> GUIManager.getMainGUI(group).open(player)));
        contents.set(4, 6, ClickableItem.of(
                GUIItemGenerator.itemGenerator("&2previous page", Material.ARROW, Collections.singletonList(""))
                ,
                e -> GUIManager.getGroupInvites(group).open(player, pagination.previous().getPage())));
        contents.set(4, 8, ClickableItem.of(
                GUIItemGenerator.itemGenerator("&cnext page", Material.ARROW, Collections.singletonList(""))
                ,
                e -> GUIManager.getGroupInvites(group).open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
