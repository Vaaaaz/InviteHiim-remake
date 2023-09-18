package me.vaaz.invitehim;

import lombok.Getter;
import lombok.Setter;
import me.vaaz.invitehim.core.cache.CodeCache;
import me.vaaz.invitehim.core.cache.RewardCache;
import me.vaaz.invitehim.core.configuration.ConfigurationHandler;
import me.vaaz.invitehim.core.database.SQLConnector;
import me.vaaz.invitehim.core.database.SQLExecutor;
import me.vaaz.invitehim.core.global.GlobalLogger;
import me.vaaz.invitehim.plugin.command.ChangeCodeCommand;
import me.vaaz.invitehim.plugin.command.CodeCommand;
import me.vaaz.invitehim.plugin.command.LookInvitesCommand;
import me.vaaz.invitehim.plugin.command.ReferenceCommand;
import me.vaaz.invitehim.plugin.event.ChatMonitorListener;
import me.vaaz.invitehim.plugin.event.ConnectionTrafficListener;
import me.vaaz.invitehim.plugin.registry.CompoundRewardRegistry;
import me.vaaz.invitehim.plugin.task.PersistentRewardTaskChecker;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter @Setter
public final class ReferenceBootstrap extends JavaPlugin {

    private GlobalLogger logTool;
    private CodeCache codeCache;
    private RewardCache rewardCache;
    private SQLConnector sqlConnector;
    private SQLExecutor sqlExecutor;
    private ConfigurationHandler configHandler;
    private CompoundRewardRegistry rewardRegistry;

    @Override
    public void onEnable() {
        this.logTool = new GlobalLogger("&b[InviteHim]");
        this.configHandler = new ConfigurationHandler(this);
        this.rewardRegistry = new CompoundRewardRegistry(this);
        this.codeCache = new CodeCache();
        this.rewardCache = new RewardCache(this);

        // Saving config. files
        this.configHandler.saveAll();

        // Registering all modules
        this.rewardRegistry.load();

        // Connect to Database
        this.connectDatabase();

        // Registering events (listeners)
        this.registerListeners(
                new ConnectionTrafficListener(this),
                new ChatMonitorListener(this)
        );

        // Registering commands
        this.registerCommands();

        // Registering runnables
        this.registerRunnables();
    }

    @Override
    public void onDisable() {
        // Disconnect from Database
        this.sqlConnector.disconnect();
    }

    /*
    Start all Runnables (tasks)
     */
    private void registerRunnables() {
        this.getServer().getScheduler().runTaskTimer(
                this, new PersistentRewardTaskChecker(this), 0L, 100L
        );
    }

    /*
    Register all Bukkit commands
     */
    private void registerCommands() {
        this.getCommand("codigo").setExecutor(new CodeCommand(this));
        this.getCommand("mudarcodigo").setExecutor(new ChangeCodeCommand(this));
        this.getCommand("ref").setExecutor(new ReferenceCommand(this));
        this.getCommand("convites").setExecutor(new LookInvitesCommand(this));
    }
    
    /*
    Use SQLConnector to connect to Database
     */
    private void connectDatabase() {
        ConfigurationSection mysql = this.configHandler.getDefaultFile().getConfigurationSection("MySQL");
        this.sqlConnector = new SQLConnector(
                "mysql",
                mysql.getString("IP"),
                mysql.getString("DB"),
                mysql.getString("User"),
                mysql.getString("Pass"));

        try {
            if (sqlConnector.connect()) {
                this.logTool.success("Conectado ao MySQL com sucesso!");
                this.sqlExecutor = sqlConnector.executor();

                String tableColumns = "id VARCHAR(16) PRIMARY KEY, code TEXT, referrerCode TEXT, timesUsed INT, currentReward INT, rewardStatus TEXT";
                sqlExecutor.createTable("invite_him", tableColumns);
            } else {
                this.logTool.fail("Não foi possível conectar-se ao MySQL.");
                this.getServer().getPluginManager().disablePlugin(this);
            }
        } catch (Exception e) {
            this.logTool.error("Não foi possível conectar-se ao banco de dados: [M -> " + e.getMessage() + " C -> " + e.getCause() + "]");
        }
    }

    /*
    Create and save files based on file name
     */
    public void saveConfig(String s) {
        s = s.endsWith(".yml") ? s : s + ".yml";
        File file = new File(this.getDataFolder(), s);
        if (this.getResource(s) != null && !file.exists())
            saveResource(s, false);
    }

    /*
    Register more than one listener per time
     */
    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

}
