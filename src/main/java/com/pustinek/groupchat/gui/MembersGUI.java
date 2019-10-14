package com.pustinek.groupchat.gui;

import com.pustinek.groupchat.managers.GUIManager;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.GUIItemGenerator;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class MembersGUI implements InventoryProvider {

    private final Group group;

    public MembersGUI(Group group) {
        this.group = group;
    }


    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();


        ArrayList<UUID> members = group.getMembers();

        ClickableItem[] items = new ClickableItem[members.size()];

        for (int i = 0; i < members.size(); i++) {
            UUID member = members.get(i);

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta skullMeta = skull.getItemMeta();
            SkullMeta testMeta = (SkullMeta) skull.getItemMeta();


            OfflinePlayer memberPlayer = Bukkit.getServer().getOfflinePlayer(member);
            if (testMeta != null) {
                testMeta.setDisplayName(memberPlayer.getName());
                testMeta.setOwningPlayer(memberPlayer);
                skull.setItemMeta(testMeta);
            }

            items[i] = ClickableItem.empty(skull);
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
                e -> GUIManager.getMembersGUI(group).open(player, pagination.previous().getPage())));
        contents.set(4, 8, ClickableItem.of(
                GUIItemGenerator.itemGenerator("&cnext page", Material.ARROW, Collections.singletonList(""))
                ,
                e -> GUIManager.getMembersGUI(group).open(player, pagination.next().getPage())));

    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
