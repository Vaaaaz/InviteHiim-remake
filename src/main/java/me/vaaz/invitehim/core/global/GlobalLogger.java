package me.vaaz.invitehim.core.global;

import me.vaaz.invitehim.core.sample.ServerPlatform;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalLogger {

    /*
    Credits to syncwrld -> github.com/syncwrld
     */

    private final String prefix;
    private final ServerPlatform platform;
    private final Logger emergencialLogger;

    public GlobalLogger(String prefix) {
        this.prefix = prefix + " ";
        this.emergencialLogger = Logger.getLogger("[StarvelAPI - GlobalLogger] ");
        platform = ServerPlatform.identify();
    }

    public void info(String message) {
        log("INFO", message, "&b");
    }

    public void warn(String message) {
        log("WARNING", message, "&e");
    }

    public void error(String message) {
        log("ERROR", message, "&c");
    }

    public void fail(String message) {
        log("FAILURE", message, "&4");
    }

    public void success(String message) {
        log("SUCCESS", message, "&a");
    }

    private void log(String level, String message, String colorCode) {
        String formattedMessage = colorize(prefix + colorCode + "[" + level + "] " + message);
        if (platform.hierarchy(ServerPlatform.BUKKIT)) {
            org.bukkit.Bukkit.getConsoleSender().sendMessage(formattedMessage);
            return;
        }

//        if (platform.hierarchy(ServerPlatform.BUNGEECORD)) {
//            net.md_5.bungee.api.ProxyServer.getInstance().getConsole().sendMessage(formattedMessage);
//            return;
//        }

        emergencialLogger.log(Level.OFF, discolorize(prefix + colorCode + "[" + level + "] " + message));
    }

    private String colorize(String s) {
        return s.replace("&", "§");
    }

    private String discolorize(String s) {
        Pattern pattern = Pattern.compile("&[0-9a-fA-F]");
        Matcher matcher = pattern.matcher(s);
        return matcher.replaceAll("");
    }

}