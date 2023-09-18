package me.vaaz.invitehim.plugin.command;

import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.plugin.views.CentralView;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LookInvitesCommand implements CommandExecutor {

    private final ReferenceBootstrap bootstrap;

    public LookInvitesCommand(ReferenceBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        boolean isConsole = !(commandSender instanceof Player);

        if (isConsole) {
            commandSender.sendMessage("Apenas jogadores podem usar este comando.");
            return false;
        }

        Player player = (Player) commandSender;
        CentralView centralView = new CentralView(
                this.bootstrap,
                player
        );
        centralView.render();

        return false;
    }

}
