package me.vaaz.invitehim.plugin.views;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Strings;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.SneakyThrows;
import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.core.configuration.ConfigurationHandler;
import me.vaaz.invitehim.core.database.SQLExecutor;
import me.vaaz.invitehim.plugin.matcher.RewardMatcher;
import me.vaaz.invitehim.plugin.utils.CustomFileConfiguration;
import me.vaaz.invitehim.plugin.utils.Heads;
import me.vaaz.invitehim.plugin.utils.ItemBuilderCustom;
import me.vaaz.invitehim.plugin.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class CentralView {

    private final ReferenceBootstrap bootstrap;
    private final CustomFileConfiguration menuConfig;
    private final SQLExecutor sqlExecutor;
    private final Player player;
    private final Gui gui;

    public CentralView(ReferenceBootstrap bootstrap, Player player) {
        this.sqlExecutor = bootstrap.getSqlExecutor();
        this.bootstrap = bootstrap;
        ConfigurationHandler configHandler = bootstrap.getConfigHandler();
        this.menuConfig = configHandler.getMenusFile();
        this.player = player;
        this.gui = Gui.gui()
                .title(Component.text(menuConfig.getString("config-menu.opcoes.titulo")))
                .rows(menuConfig.getInt("config-menu.opcoes.tamanho"))
                .disableAllInteractions()
                .create();
    }

    @SneakyThrows
    public void render() {
        ConfigurationSection items_section = this.menuConfig.getConfigurationSection("config-menu.itens");
        RewardMatcher rewardMatcher = new RewardMatcher(this.bootstrap.getRewardRegistry().getRewardSet());
        if (items_section == null) return;

        String needString = "";
        var rewardStatus = sqlExecutor.getString("invite_him", player.getName(), "rewardStatus");

        int currentInvites = sqlExecutor.getInt("invite_him", player.getName(), "timesUsed");
        if ("complete".equals(rewardStatus)) {
            needString = "nenhum";
        } else {
            int necessaryInvites = rewardMatcher.getRewardOfPriority(
                    this.bootstrap.getRewardCache().getRewardPriority(player)
            ).get().getNecessaryInvites();

            needString = String.valueOf(necessaryInvites - currentInvites).replace(".0", "");
        }

        for (String key : items_section.getKeys(false)) {
            ItemStack rawStack = null;
            String material = items_section.getString(key + ".material");

            if (material != null && material.startsWith("HEAD->")) {
                String url = "http://textures.minecraft.net/texture//";
                rawStack = Heads.getSkull(url + material.replace("HEADS->", ""));
            } else {
                rawStack = new ItemStack(XMaterial.matchXMaterial(material).get().parseMaterial());
            }

            var invitedBy = sqlExecutor.getString("invite_him", player.getName(), "referrerCode");
            invitedBy = Strings.isNullOrEmpty(invitedBy) ? "Ninguém" : invitedBy.split(",")[1];

            String finalInvitedBy = invitedBy;
            String finalNeedString = needString;
            ItemStack itemStack = new ItemBuilderCustom(rawStack)
                    .setName(TextUtils.translate(items_section.getString(key + ".nome")))
                    .setLore(items_section.getStringList(key + ".descricao").stream().map(
                            line -> {
                                return TextUtils.translate(line
                                                .replace("{player}", player.getName())
                                                .replace("{convites}", String.valueOf(currentInvites).replace(".0", ""))
                                                .replace("{convidado}", finalInvitedBy))
                                        .replace("{proximo}", finalNeedString);
                            }
                    ).collect(Collectors.toList()))
                    .toItemStack();

            GuiItem guiItem = ItemBuilder.from(itemStack).asGuiItem();
            this.gui.setItem(items_section.getInt(key + ".slot"), guiItem);
        }

        this.gui.open(player);
    }

}
