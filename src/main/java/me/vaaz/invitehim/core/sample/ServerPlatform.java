package me.vaaz.invitehim.core.sample;

public enum ServerPlatform {

    /*
    Credits to syncwrld -> github.com/syncwrld
     */

    BUKKIT, SPIGOT, PAPER, BUNGEECORD, VELOCITY;

    public static ServerPlatform identify() {
        if (isClassAvailable("com.velocitypowered.api.proxy.ProxyServer")) {
            return VELOCITY;
        }
        if (isClassAvailable("net.md_5.bungee.api.ProxyServer")) {
            return BUNGEECORD;
        }
        if (isClassAvailable("io.papermc.paperclip.Agent")) {
            return PAPER;
        }
        if (isClassAvailable("org.spigotmc.event.entity.EntityMountEvent")) {
            return SPIGOT;
        }
        if (isClassAvailable("org.bukkit.plugin.java.JavaPlugin")) {
            return BUKKIT;
        }
        return null;
    }

    private static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public boolean hierarchy(ServerPlatform comparable) {
        return (this == comparable) ||
               (this == BUKKIT && (comparable == PAPER || comparable == SPIGOT)) ||
               (this == PAPER && (comparable == BUKKIT || comparable == SPIGOT)) ||
               (this == SPIGOT && (comparable == BUKKIT || comparable == PAPER));
    }
}