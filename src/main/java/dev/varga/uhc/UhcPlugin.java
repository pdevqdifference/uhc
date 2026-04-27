package dev.varga.uhc;

import dev.varga.uhc.command.UhcCommand;
import dev.varga.uhc.game.GameManager;
import dev.varga.uhc.listener.GameRulesListener;
import dev.varga.uhc.listener.PlayerCombatListener;
import dev.varga.uhc.listener.PlayerDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class UhcPlugin extends JavaPlugin {
    private GameManager gameManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.gameManager = new GameManager(this);

        getCommand("uhc").setExecutor(new UhcCommand(this, gameManager));

        Bukkit.getPluginManager().registerEvents(new PlayerCombatListener(gameManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(gameManager), this);
        Bukkit.getPluginManager().registerEvents(new GameRulesListener(gameManager), this);
    }

    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.forceStop();
        }
    }

    public void reloadPluginConfig() {
        reloadConfig();
        gameManager.reloadFromConfig();
    }
}

