package dev.varga.uhc.listener;

import dev.varga.uhc.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class PlayerDeathListener implements Listener {
    private final GameManager gameManager;

    public PlayerDeathListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player dead = e.getEntity();
        if (!gameManager.isAlive(dead.getUniqueId())) return;

        e.setDeathMessage(ChatColor.GRAY + "[UHC] " + ChatColor.RED + dead.getName() + " ha muerto.");
        gameManager.handleDeath(dead);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (!gameManager.isAlive(p.getUniqueId())) {
            // Spectator players respawn at world spawn
            e.setRespawnLocation(p.getWorld().getSpawnLocation());
        }
    }
}

