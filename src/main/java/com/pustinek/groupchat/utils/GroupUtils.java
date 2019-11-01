package com.pustinek.groupchat.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupUtils {

    public static boolean validateGroupName(String name) {
        String regex = "^[a-zA-Z0-9_-]*$";
        return name.matches(regex);
    }

    public static boolean validateGroupNameLength(String name) {
        return name.length() >= 3 && name.length() <= 12;
    }

    public static boolean validateGroupPrefix(String prefix) {
        String regex = "^[a-zA-Z0-9_-]*$";
        int length = 10;
        return prefix.matches(regex);
    }

    public static int getPlayerGroupLimit(Player player) {
        Set<PermissionAttachmentInfo> effectivePermissions = player.getEffectivePermissions();
        Pattern limitPattern = Pattern.compile("(groupchat.limit).([0-9]+)");
        Iterator<PermissionAttachmentInfo> itr = effectivePermissions.iterator();

        int limit = 0;
        while (itr.hasNext()) {
            PermissionAttachmentInfo info = itr.next();
            String permission = info.getPermission();
            Matcher matcher = limitPattern.matcher(permission);
            if (matcher.find()) {
                int foundLimit = Integer.parseInt(matcher.group(2));
                if (foundLimit > limit) {
                    limit = foundLimit;
                }
            }
        }
        return limit;
    }


}
