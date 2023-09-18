package me.vaaz.invitehim.core.cache;

import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.plugin.matcher.RewardMatcher;
import me.vaaz.invitehim.plugin.registry.CompoundRewardRegistry;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class RewardCache {

    private final RewardMatcher rewardMatcher;
    private final ConcurrentHashMap<Player, Integer> codeMapObj;

    public RewardCache(ReferenceBootstrap bootstrap) {
        CompoundRewardRegistry rewardRegistry = bootstrap.getRewardRegistry();
        this.rewardMatcher = new RewardMatcher(rewardRegistry.getRewardSet());
        this.codeMapObj = new ConcurrentHashMap<>();
    }

    /*
    Define the player reward priority on cache map
     */
    public void setRwPriority(Player player, int code) {
        this.codeMapObj.put(player, code);
    }

    /*
    Get player reward priority from cache map, if not contains returns them minor possible
     */
    public int getRewardPriority(Player player) {
        return codeMapObj.getOrDefault(player, this.rewardMatcher.getMinor().get().getPriority());
    }

}
