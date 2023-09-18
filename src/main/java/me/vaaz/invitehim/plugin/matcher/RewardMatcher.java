package me.vaaz.invitehim.plugin.matcher;

import me.vaaz.invitehim.plugin.model.CompoundReward;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

public class RewardMatcher {

    private final Set<CompoundReward> rewards;

    public RewardMatcher(Set<CompoundReward> rewards) {
        this.rewards = rewards;
    }

    public Optional<CompoundReward> getMinor() {
        Comparator<CompoundReward> comparator = Comparator.comparingInt(CompoundReward::getPriority);
        return rewards.stream().min(comparator);
    }

    public Optional<CompoundReward> getMajor() {
        Comparator<CompoundReward> comparator = Comparator.comparingInt(CompoundReward::getPriority);
        return rewards.stream().max(comparator);
    }

    public Optional<CompoundReward> getRewardOfPriority(int i) {
        return rewards.stream().filter(filter -> filter.getPriority() == i).findAny();
    }

}
