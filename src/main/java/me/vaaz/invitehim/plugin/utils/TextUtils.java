package me.vaaz.invitehim.plugin.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class TextUtils {

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translate(List<String> stringList) {
        return stringList.stream().map(TextUtils::translate).collect(Collectors.toList());
    }

}
