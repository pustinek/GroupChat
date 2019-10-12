package com.pustinek.groupchat.commands;

import com.pustinek.groupchat.Main;
import com.pustinek.groupchat.models.Group;
import org.bukkit.command.CommandSender;

public class CommandTest extends CommandDefault {

    private final Main plugin;

    public CommandTest(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getCommandStart() {
        return "groupchat test";
    }

    @Override
    public String getHelp(CommandSender target) {
        return "help-test";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {


        if (args.length < 2) {
            return;
        }
        String groupName = args[1];

        Group group = Main.getGroupManager().getGroupClone(groupName);

        Main.debug("================TEST=================");
        Main.debug(group.getName());
        Main.debug(group.getPrefix());
        Main.debug(group.getOwner().toString());
        group.setName("test!!");
        Main.debug(group.getName());


    }
}
