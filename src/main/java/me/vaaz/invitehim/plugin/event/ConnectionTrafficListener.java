package me.vaaz.invitehim.plugin.event;

import lombok.SneakyThrows;
import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.core.cache.CodeCache;
import me.vaaz.invitehim.core.cache.RewardCache;
import me.vaaz.invitehim.core.database.SQLExecutor;
import me.vaaz.invitehim.plugin.matcher.RewardMatcher;
import me.vaaz.invitehim.plugin.mechanic.StaticCodeMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class ConnectionTrafficListener implements Listener {

    private final ReferenceBootstrap bootstrap;

    public ConnectionTrafficListener(ReferenceBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @SneakyThrows
    @EventHandler(priority = EventPriority.HIGH)
    public void whenPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String playerName = event.getPlayer().getName();

        final CodeCache codeCache = this.bootstrap.getCodeCache();
        final RewardCache rewardCache = this.bootstrap.getRewardCache();
        final RewardMatcher rewardMatcher = new RewardMatcher(this.bootstrap.getRewardRegistry().getRewardSet());
        final SQLExecutor sqlExecutor = this.bootstrap.getSqlExecutor();
        final String randomCode = StaticCodeMethod.generateRandomKey();

        int minorPriority = rewardMatcher.getMinor().get().getPriority();
        sqlExecutor.playerFirstValues(
                playerName,
                randomCode,
                minorPriority
        );

        int currentRwPriority = sqlExecutor.getInt(
                "invite_him",
                playerName,
                "currentReward"
        );

        rewardMatcher.getRewardOfPriority(currentRwPriority).ifPresentOrElse(compoundReward -> {
            rewardCache.setRwPriority(player, compoundReward.getPriority());
        }, () -> {
            sqlExecutor.update(
                    "invite_him",
                    playerName,
                    "currentReward",
                    minorPriority);
            rewardCache.setRwPriority(player, minorPriority);
        });

        Bukkit.getScheduler().runTaskLater(this.bootstrap, () -> {
            String currentCode = null;

            try {
                currentCode = sqlExecutor.getString("invite_him", playerName, "code");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (currentCode == null) {
                this.bootstrap.getLogTool().info("Current code is null, but: " + randomCode + " is the random code and null is the current code. (@1)");
                return;
            }

            codeCache.setCodeIfNull(player, currentCode);
        }, 20L);
    }

}
