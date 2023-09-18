package me.vaaz.invitehim.core.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import me.vaaz.invitehim.ReferenceBootstrap;
import me.vaaz.invitehim.plugin.utils.CustomFileConfiguration;

@Getter(AccessLevel.PUBLIC)
public class ConfigurationHandler {

    private final ReferenceBootstrap bootstrap;
    private final CustomFileConfiguration defaultFile;
    private final CustomFileConfiguration rewardsFile;
    private final CustomFileConfiguration menusFile;
    private final CustomFileConfiguration messagesFile;

    @SneakyThrows
    public ConfigurationHandler(ReferenceBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.defaultFile = new CustomFileConfiguration("configuration", this.bootstrap);
        this.rewardsFile = new CustomFileConfiguration("rewards", this.bootstrap);
        this.menusFile = new CustomFileConfiguration("menu", this.bootstrap);
        this.messagesFile = new CustomFileConfiguration("messages", this.bootstrap);
    }

    public void saveAll() {
        /*
        Create if not exists
         */
        this.bootstrap.saveConfig("configuration");
        this.bootstrap.saveConfig("rewards");
        this.bootstrap.saveConfig("menu");
        this.bootstrap.saveConfig("messages");

        /*
        Save the content
         */
        this.defaultFile.save();
        this.rewardsFile.save();
        this.menusFile.save();
        this.messagesFile.save();
    }

    public long reloadAll() {
        val start = System.currentTimeMillis();

        this.saveAll();
        /*
        Reload all files
         */
        this.defaultFile.reload();
        this.rewardsFile.reload();
        this.menusFile.reload();
        this.messagesFile.reload();

        /*
        Return the reload time in millis
         */
        return (System.currentTimeMillis() - start);
    }

}
