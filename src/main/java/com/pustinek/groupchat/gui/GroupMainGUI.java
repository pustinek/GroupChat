package com.pustinek.groupchat.gui;

import com.pustinek.groupchat.managers.GUIManager;
import com.pustinek.groupchat.models.Group;
import com.pustinek.groupchat.utils.GUIItemGenerator;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class GroupMainGUI implements InventoryProvider {

    private final Group group;

    public GroupMainGUI(Group group) {
        this.group = group;
    }

    @Override
    public void init(Player player, InventoryContents contents) {


        ItemStack basicInfo = GUIItemGenerator.itemGenerator(
                "&2Basic info",
                Material.SPRUCE_SIGN,
                Arrays.asList(
                        "members: &f" + group.getMembers().size(),
                        "type: &f" + group.getType(),
                        "tier: &f" + group.getTier()
                )
        );


        ItemStack membersItemStack = GUIItemGenerator.itemGenerator(
                "&2Members",
                Material.SKELETON_SKULL,
                Collections.singletonList("total members: &f" + group.getMembers().size())
        );

        ItemStack invitesItemStack = GUIItemGenerator.itemGenerator(
                "&2Invited Players",
                Material.BOOK,
                Collections.singletonList("")
        );


        contents.set(0, 0, ClickableItem.empty(basicInfo));

        contents.set(0, 1, ClickableItem.of(membersItemStack, e -> {
            if (e.getClick() == ClickType.LEFT) {
                GUIManager.getMembersGUI(group).open(player);

            }

        }));
        contents.set(0, 2, ClickableItem.of(invitesItemStack, e -> {
            if (e.getClick() == ClickType.LEFT) {
                GUIManager.getGroupInvites(group).open(player);

            }

        }));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
