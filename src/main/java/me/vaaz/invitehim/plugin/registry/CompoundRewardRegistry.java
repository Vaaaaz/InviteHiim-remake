package me.vaaz.invitehim.plugin.registry;

import lombok.Getter;
import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.plugin.model.CompoundReward;
import me.vaaz.invitehim.plugin.utils.CustomFileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CompoundRewardRegistry {

    private final ReferenceBootstrap bootstrap;
    private final Set<CompoundReward> rewardSet;

    public CompoundRewardRegistry(ReferenceBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.rewardSet = new HashSet<>();
    }

    public void load() {
        this.rewardSet.clear();

        CustomFileConfiguration rewardsFile = this.bootstrap.getConfigHandler().getRewardsFile();
        ConfigurationSection rewardSection = rewardsFile.getConfigurationSection("recompensas");
        if (rewardSection == null) return;

        for (String key : rewardSection.getKeys(false)) {
            this.rewardSet.add(new CompoundReward(
                    rewardSection.getString(key + ".titulo"),
                    rewardSection.getInt(key + ".convites"),
                    rewardSection.getInt(key + ".prioridade"),
                    rewardSection.getStringList(key + ".comandos")
                    , rewardSection.getStringList(key + ".chat")
            ));
        }

        this.bootstrap.getLogTool().success("Foram registrados " + rewardSet.size() + " tipos de recompensas.");
    }

}
