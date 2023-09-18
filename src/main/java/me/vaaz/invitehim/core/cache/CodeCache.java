package me.vaaz.invitehim.core.cache;

import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class CodeCache {

    private final ConcurrentHashMap<Player, String> codeMapObj;

    public CodeCache() {
        this.codeMapObj = new ConcurrentHashMap<>();
    }

    /*
    Define the player code on cache map
     */
    public void setCode(Player player, String code) {
        this.codeMapObj.put(player, code);
    }

    /*
    Define the player code on cache map only if current is null
     */
    public void setCodeIfNull(Player player, String code) {
        if (this.getCode(player) == null)
            this.setCode(player, code);
    }

    /*
    Get player code from cache map, if not contains returns null
     */
    public String getCode(Player player) {
        return codeMapObj.getOrDefault(player, null);
    }

}
