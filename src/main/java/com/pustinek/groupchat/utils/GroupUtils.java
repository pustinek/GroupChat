package com.pustinek.groupchat.utils;

public class GroupUtils {

    public static boolean validateGroupName(String name) {
        String regex = "^[a-zA-Z0-9_-]*$";
        return name.matches(regex);
    }

    public static boolean validateGroupPrefix(String prefix) {
        String regex = "^[a-zA-Z0-9_-]*$";
        int length = 10;
        return prefix.matches(regex);
    }
}
