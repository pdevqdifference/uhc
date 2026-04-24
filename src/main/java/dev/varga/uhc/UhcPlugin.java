package dev.pdevqdifference.uhc;

import dev.pdevqdifference.uhc.command.UhcCommand;
import dev.pdevqdifference.uhc.game.GameManager;
import dev.pdevqdifference.uhc.listener.GameRulesListener;
import dev.pdevqdifference.uhc.listener.PlayerCombatListener;
import dev.pdevqdifference.uhc.listener.PlayerDeathListener;
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

