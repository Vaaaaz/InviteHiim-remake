package me.vaaz.invitehim.plugin.event;

import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.core.database.SQLExecutor;
import me.vaaz.invitehim.core.map.ListMap;
import me.vaaz.invitehim.plugin.mechanic.StaticCodeMethod;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatMonitorListener implements Listener {

    private final ReferenceBootstrap bootstrap;

    public ChatMonitorListener(ReferenceBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @EventHandler
    public void whenPlayerInsertNewCode(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!ListMap.getChangeCodeList().contains(player))
            return;

        event.setCancelled(true);
        final SQLExecutor sqlExecutor = this.bootstrap.getSqlExecutor();
        String message = event.getMessage();

        if (message.length() > 16) {
            player.sendMessage("§cO código não pode ter mais de 16 caracteres.");
            return;
        }

        if (message.length() < 12) {
            player.sendMessage("§cO código não pode ter menos de 12 caracteres.");
            return;
        }

        for (char c : message.toCharArray()) {
            if (!StaticCodeMethod.getAllowedChars().contains(c)) {
                player.sendMessage("§cO código não pode conter o caractere: '" + c + "'.");
                return;
            }
        }

        List<Player> playerList = ListMap.getChangeCodeList();
        playerList.remove(player);
        ListMap.setChangeCodeList(playerList);

        sqlExecutor.update(
                "invite_him",
                player.getName(),
                "code",
                message
        );
        this.bootstrap.getCodeCache().setCode(player, message);
        player.playSound(
                player.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1, 1
        );
        player.sendMessage(
                "",
                "§e§lCÓDIGO DE REFERÊNCIA",
                " §fVocê alterou seu código de referência",
                " §fNovo código: §e" + message,
                ""
        );
    }

}
