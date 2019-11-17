package com.pustinek.groupchat.managers;


import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.gui.GroupInvites;
import com.pustinek.groupchat.gui.GroupMainGUI;
import com.pustinek.groupchat.gui.InvitesGUI;
import com.pustinek.groupchat.gui.MembersGUI;
import com.pustinek.groupchat.models.Group;
import fr.minuskube.inv.SmartInventory;

public class GUIManager extends Manager {

    private final Main plugin;

    public GUIManager(Main plugin) {
        this.plugin = plugin;
    }

    public static SmartInventory getMembersGUI(Group group) {
        return SmartInventory.builder()
                .manager(Main.getInventoryManager())
                .provider(new MembersGUI(group))
                .size(5, 9)
                .title("Members of " + group.getName())
                .build();
    }

    public static SmartInventory getMainGUI(Group group) {
        return SmartInventory.builder()
                .manager(Main.getInventoryManager())
                .provider(new GroupMainGUI(group))
                .size(4, 9)
                .title("Group info - " + group.getName())
                .build();
    }

    public static SmartInventory getGroupInvites(Group group) {
        return SmartInventory.builder()
                .manager(Main.getInventoryManager())
                .provider(new GroupInvites(group))
                .size(5, 9)
                .title("Invited players")
                .build();
    }

    public static SmartInventory getInvitesGUI() {
        return SmartInventory.builder()
                .manager(Main.getInventoryManager())
                .provider(new InvitesGUI())
                .size(5, 9)
                .title("Your invites to groups")
                .build();
    }


}



