package com.pustinek.mctemplate.managers;

import com.pustinek.mctemplate.Main;
import com.pustinek.mctemplate.commands.CommandDefault;
import com.pustinek.mctemplate.commands.CommandExample;
import com.pustinek.mctemplate.utils.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class CommandManager extends Manager implements CommandExecutor, TabCompleter {

    private final ArrayList<CommandDefault> commands;
    private Main plugin;

    public CommandManager(Main plugin) {

        commands = new ArrayList<>();
        //ADD: Here you add the commands that you want to use..
        commands.add(new CommandExample(plugin));


        plugin.getCommand(Permissions.PLUGIN_NAME).setExecutor(this);
        plugin.getCommand(Permissions.PLUGIN_NAME).setTabCompleter(this);

        // store plugin instance
        this.plugin = plugin;
    }


    private void showHelp(CommandSender target) {

        // Add all messages to a list
        ArrayList<String> messages = new ArrayList<>();

        for (CommandDefault command : commands) {
            String help = command.getHelp(target);
            if (help != null && help.length() != 0) {
                messages.add(help);
            }
        }

        for (String message : messages) {
            Main.messageNoPrefix(target, message);
        }


    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean executed = false;
        for (int i = 0; i < commands.size() && !executed; i++) {
            if (commands.get(i).canExecute(command, args)) {
                commands.get(i).execute(sender, args);
                executed = true;
            }
        }
        if (!executed && args.length == 0) {
            this.showHelp(sender);
        } else if (!executed) {
            Main.message(sender, " Command is not valid");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (!sender.hasPermission(Permissions.TAB_COMPLETE)) {
            return result;
        }
        int toCompleteNumber = args.length;
        String toCompletePrefix = args[args.length - 1].toLowerCase();


        if (toCompleteNumber == 1) {
            for (CommandDefault c : commands) {
                String begin = c.getCommandStart();
                result.add(begin.substring(begin.indexOf(' ') + 1));
            }
        } else {
            String[] start = new String[args.length];
            start[0] = command.getName();
            System.arraycopy(args, 0, start, 1, args.length - 1);
            for (CommandDefault c : commands) {
                if (c.canExecute(command, args)) {
                    result = c.getTabCompleteList(toCompleteNumber, start, sender);
                }
            }
        }
        // Filter and sort the results
        if (!result.isEmpty()) {
            SortedSet<String> set = new TreeSet<>();
            for (String suggestion : result) {
                if (suggestion.toLowerCase().startsWith(toCompletePrefix)) {
                    set.add(suggestion);
                }
            }
            result.clear();
            result.addAll(set);
        }
        return result;

    }
}
