package dev.breiner.uhc;

import dev.breiner.uhc.command.UhcCommand;
import dev.breiner.uhc.game.GameManager;
import dev.breiner.uhc.host.HostManager;
import dev.breiner.uhc.listener.GameRulesListener;
import dev.breiner.uhc.listener.FreezeListener;
import dev.breiner.uhc.listener.LateJoinListener;
import dev.breiner.uhc.listener.PlayerCombatListener;
import dev.breiner.uhc.listener.PlayerDeathListener;
import dev.breiner.uhc.scenario.ScenarioManager;
import dev.breiner.uhc.scenario.impl.CutCleanScenario;
import dev.breiner.uhc.scenario.impl.HasteyBoysScenario;
import dev.breiner.uhc.scenario.impl.TimberScenario;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class UhcPlugin extends JavaPlugin {
    private GameManager gameManager;
    private ScenarioManager scenarioManager;
    private HostManager hostManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.gameManager = new GameManager(this);
        this.hostManager = new HostManager();
        this.scenarioManager = new ScenarioManager(this);

        this.scenarioManager.register(new CutCleanScenario(gameManager));
        this.scenarioManager.register(new TimberScenario(gameManager));
        this.scenarioManager.register(new HasteyBoysScenario(gameManager));

        applyScenarioConfig();

        if (getCommand("uhc") != null) {
            UhcCommand cmd = new UhcCommand(this, gameManager, scenarioManager, hostManager);
            getCommand("uhc").setExecutor(cmd);
            getCommand("uhc").setTabCompleter(cmd);
        }

        Bukkit.getPluginManager().registerEvents(new PlayerCombatListener(gameManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(gameManager), this);
        Bukkit.getPluginManager().registerEvents(new GameRulesListener(gameManager), this);
        Bukkit.getPluginManager().registerEvents(new FreezeListener(hostManager), this);
        Bukkit.getPluginManager().registerEvents(new LateJoinListener(gameManager, hostManager), this);
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.forceStop();
        }
        if (scenarioManager != null) {
            scenarioManager.disableAll();
        }
    }

    public void reloadPluginConfig() {
        reloadConfig();
        gameManager.reloadFromConfig();
        applyScenarioConfig();
    }

    private void applyScenarioConfig() {
        if (scenarioManager == null) return;

        scenarioManager.disableAll();

        java.util.Set<String> keys = getConfig().getConfigurationSection("scenarios") == null
                ? java.util.Collections.<String>emptySet()
                : getConfig().getConfigurationSection("scenarios").getKeys(false);

        boolean anyEnabled = false;
        for (String key : keys) {
            if (getConfig().getBoolean("scenarios." + key, false)) {
                scenarioManager.enable(key);
                anyEnabled = true;
            }
        }

        if (!anyEnabled) {
            java.util.List<dev.breiner.uhc.scenario.Scenario> all = new java.util.ArrayList<>(scenarioManager.getAll());
            if (!all.isEmpty()) {
                int idx = new java.util.Random().nextInt(all.size());
                String picked = all.get(idx).getName();
                scenarioManager.enable(picked);
                getLogger().info("No scenarios enabled in config. Randomly enabled: " + picked);
            }
        }
    }
}

