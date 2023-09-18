package me.vaaz.invitehim.plugin.task;

import lombok.SneakyThrows;
import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.core.cache.RewardCache;
import me.vaaz.invitehim.core.database.SQLExecutor;
import me.vaaz.invitehim.plugin.matcher.RewardMatcher;
import me.vaaz.invitehim.plugin.model.CompoundReward;
import me.vaaz.invitehim.plugin.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PersistentRewardTaskChecker implements Runnable {

    private final ReferenceBootstrap bootstrap;
    private final RewardCache rewardCache;
    private final RewardMatcher rewardMatcher;
    private final SQLExecutor sqlExecutor;

    public PersistentRewardTaskChecker(ReferenceBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.rewardCache = this.bootstrap.getRewardCache();
        this.rewardMatcher = new RewardMatcher(this.bootstrap.getRewardRegistry().getRewardSet());
        this.sqlExecutor = this.bootstrap.getSqlExecutor();
    }

    @SneakyThrows
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int playerRewardPriority = this.rewardCache.getRewardPriority(player);
            Optional<CompoundReward> optionalCompoundReward = this.rewardMatcher.getRewardOfPriority(playerRewardPriority);

            if (optionalCompoundReward.isEmpty()) {
                continue;
            }

            CompoundReward compoundReward = optionalCompoundReward.get();
            int necessaryInvites = compoundReward.getNecessaryInvites();
            int currentInvites = sqlExecutor.getInt("invite_him", player.getName(), "timesUsed");

            if (currentInvites >= necessaryInvites) {
                String rewardStatus = sqlExecutor.getString("invite_him", player.getName(), "rewardStatus");
                if (rewardStatus != null && (rewardStatus.equals("complete")))
                    continue;

                compoundReward.getExecutableCommands().forEach(command -> {
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            command.replace("%player%", player.getName())
                    );
                });

                compoundReward.getChatMessages().forEach(message -> {
                    player.sendMessage(TextUtils.translate(
                            message.replace("%player%", player.getName())
                    ));
                });

                if (this.rewardMatcher.getMajor().get().getPriority() < (playerRewardPriority + 1)) {
                    this.rewardCache.setRwPriority(player, this.rewardMatcher.getMajor().get().getPriority());
                    this.sqlExecutor.update(
                            "invite_him", player.getName(),
                            "rewardStatus", "complete"
                    );
                } else {
                    this.rewardCache.setRwPriority(player, playerRewardPriority + 1);
                    this.sqlExecutor.update(
                            "invite_him", player.getName(),
                            "currentReward", playerRewardPriority + 1
                    );
                    this.sqlExecutor.update(
                            "invite_him", player.getName(),
                            "rewardStatus", "pending"
                    );
                }

            }
        }
    }
}
