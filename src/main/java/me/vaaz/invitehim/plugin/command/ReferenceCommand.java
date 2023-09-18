package me.vaaz.invitehim.plugin.command;

import com.cryptomorin.xseries.messages.Titles;
import lombok.SneakyThrows;
import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.core.configuration.ConfigurationHandler;
import me.vaaz.invitehim.plugin.utils.CustomFileConfiguration;
import me.vaaz.invitehim.plugin.utils.TextUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

public class ReferenceCommand implements CommandExecutor {

    private final ReferenceBootstrap bootstrap;
    private final CustomFileConfiguration messagesFile;;

    public ReferenceCommand(ReferenceBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        ConfigurationHandler configHandler = bootstrap.getConfigHandler();
        this.messagesFile = configHandler.getMessagesFile();
    }


    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        boolean isConsole = !(commandSender instanceof Player);

        if (isConsole) {
            commandSender.sendMessage("Apenas jogadores podem executar este comando");
            return false;
        }

        Player player = (Player) commandSender;
        Audience audience = BukkitAudiences.create(bootstrap).player(player);

        if (args.length == 0) {
            audience.sendActionBar(Component.text("§cINCORRETO! §7/referencia [código]"));
            return false;
        }

        String referrerCode = this.bootstrap.getSqlExecutor().getString("invite_him", player.getName(), "referrerCode");
        if (referrerCode != null) {
            audience.sendActionBar(Component.text(this.messagesFile.getString("alreadyUsed").replace("&", "§").replace("{referrerCode}", referrerCode.split(",")[1])));
            return false;
        }

        String referenceCode = args[0];
        String playerByCode = this.bootstrap.getSqlExecutor().getPlayerByCode(referenceCode);

        if (playerByCode == null) {
            audience.sendActionBar(Component.text(TextUtils.translate(this.messagesFile.getString("notExist"))));
            return false;
        }

        if (playerByCode.equals(player.getName())) {
            audience.sendActionBar(Component.text("§cVocê não pode referenciar a si mesmo!"));
            return false;
        }

        this.bootstrap.getSqlExecutor().update(
                "invite_him",
                playerByCode,
                "timesUsed",
                this.bootstrap.getSqlExecutor().getInt(
                        "invite_him", playerByCode, "timesUsed"
                ) + 1
        );

        this.bootstrap.getSqlExecutor().update(
                "invite_him",
                player.getName(),
                "referrerCode",
                referenceCode + "," + playerByCode
        );

        player.sendTitle(
                TextUtils.translate(this.messagesFile.getString("title-code-used-self").split("<nl>")[0].replace("{player}", playerByCode)), TextUtils.translate(this.messagesFile.getString("title-code-used-self").split("<nl>")[1].replace("{player}", playerByCode)), 20, 40, 20
        );

        Player referentiatorPlayer = Bukkit.getPlayerExact(playerByCode);
        if (referentiatorPlayer != null && referentiatorPlayer.isOnline()) {
            Audience referenceAudience = BukkitAudiences.create(bootstrap).player(referentiatorPlayer);
            referentiatorPlayer.sendTitle(
                    TextUtils.translate(this.messagesFile.getString("title-code-used").split("<nl>")[0]),
                    TextUtils.translate(this.messagesFile.getString("title-code-used").split("<nl>")[1]), 20, 40, 20
            );

            referenceAudience.sendMessage(
                    Component.text(TextUtils.translate(this.messagesFile.getString("codeUsed")).replace("{playerName}", player.getName()))
                            .clickEvent(ClickEvent.runCommand("/invites"))
            );
        }

        return false;
    }

}
