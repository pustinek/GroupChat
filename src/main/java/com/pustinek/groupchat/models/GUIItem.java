package com.pustinek.groupchat.models;


import org.bukkit.inventory.ItemStack;

public class GUIItem {
    private ItemStack itemStack;
    private int slot;

    public GUIItem(ItemStack itemStack, int slot) {
        this.itemStack = itemStack;
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }
}
