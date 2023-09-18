package me.vaaz.invitehim.plugin.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor @Getter
public class CompoundReward {

    private final String rewardName;
    private final int necessaryInvites;
    private final int priority;
    private final List<String> executableCommands;
    private final List<String> chatMessages;

}
