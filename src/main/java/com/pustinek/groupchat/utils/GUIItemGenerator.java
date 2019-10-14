package com.pustinek.groupchat.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GUIItemGenerator {

    public static ItemStack itemGenerator(String displayName, Material material, List<String> lore, Integer amount) {
        ItemStack is = new ItemStack(material, amount);
        ItemMeta itemMeta = is.getItemMeta();
        if (itemMeta == null) return is;
        itemMeta.setDisplayName(ChatUtils.chatColor(displayName));
        if (lore != null)
            itemMeta.setLore(ChatUtils.chatColor(lore));
        is.setItemMeta(itemMeta);
        return is;
    }

    public static ItemStack itemGenerator(String displayName, Material material, List<String> lore) {
        return itemGenerator(displayName, material, lore, 1);
    }

}
