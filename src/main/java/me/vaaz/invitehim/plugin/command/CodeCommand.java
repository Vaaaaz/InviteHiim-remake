package me.vaaz.invitehim.plugin.command;

import lombok.SneakyThrows;
import lombok.val;
import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.core.cache.CodeCache;
import me.vaaz.invitehim.core.configuration.ConfigurationHandler;
import me.vaaz.invitehim.core.database.SQLExecutor;
import me.vaaz.invitehim.plugin.mechanic.StaticCodeMethod;
import me.vaaz.invitehim.plugin.utils.CustomFileConfiguration;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CodeCommand implements CommandExecutor {

    private final ReferenceBootstrap bootstrap;
    private final CustomFileConfiguration messagesFile;;

    public CodeCommand(ReferenceBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        ConfigurationHandler configHandler = bootstrap.getConfigHandler();
        this.messagesFile = configHandler.getMessagesFile();
    }

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        boolean isConsole = !(commandSender instanceof Player);

        if (isConsole) {
            commandSender.sendMessage("§cApenas jogadores podem executar esse comando.");
            return false;
        }

        final Player player = (Player) commandSender;

        CodeCache cache = this.bootstrap.getCodeCache();
        String playerCode = cache.getCode(player);
        String playerName = player.getName();

        Audience audience = BukkitAudiences.create(bootstrap).player(player);

        if (playerCode == null) {
            player.sendMessage(this.messagesFile.getString("invalid").replace("&", "§"));
        } else {
            val inviteMessage = this.messagesFile.getString("inviteMessage").replace("&", "§").replace("{playerName}", playerName).replace("{playerCode}", playerCode);

            audience.sendMessage(
                    Component.text(this.messagesFile.getString("yourCode").replace("{playerCode}", playerCode).replace("&", "§").replace("{playerName}", playerName))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, inviteMessage))
            );
        }

        return false;
    }

}
