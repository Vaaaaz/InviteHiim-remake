package me.vaaz.invitehim.plugin.command;

import dev.triumphteam.gui.guis.Gui;
import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.core.configuration.ConfigurationHandler;
import me.vaaz.invitehim.core.map.ListMap;
import me.vaaz.invitehim.plugin.utils.CustomFileConfiguration;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ChangeCodeCommand implements CommandExecutor {

    private final ReferenceBootstrap bootstrap;
    private final CustomFileConfiguration messagesFile;;

    public ChangeCodeCommand(ReferenceBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        ConfigurationHandler configHandler = bootstrap.getConfigHandler();
        this.messagesFile = configHandler.getMessagesFile();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        boolean isConsole = !(commandSender instanceof Player);

        if (isConsole) {
            commandSender.sendMessage("§cApenas jogadores podem executar esse comando.");
            return false;
        }

        Player player = (Player) commandSender;
        List<Player> playerList = ListMap.getChangeCodeList();

        if (playerList.contains(player)) {
            player.sendMessage(this.messagesFile.getString("changecode").replace("&", "§"));
        } else {
            player.sendMessage(this.messagesFile.getString("newcode").replace("&", "§"));
            playerList.add(player);
            ListMap.setChangeCodeList(playerList);
        }

        return false;
    }

}
