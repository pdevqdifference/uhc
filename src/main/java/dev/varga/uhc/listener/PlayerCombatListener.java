package dev.varga.uhc.listener;

import dev.varga.uhc.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class PlayerCombatListener implements Listener {
    private final GameManager gameManager;

    public PlayerCombatListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!gameManager.isGraceActive()) return;

        Entity damager = e.getDamager();
        Entity victim = e.getEntity();

        Player damagerPlayer = (damager instanceof Player) ? (Player) damager : null;
        if (damagerPlayer == null) return;
        if (!(victim instanceof Player)) return;

        e.setCancelled(true);
        damagerPlayer.sendMessage(ChatColor.RED + "[UHC] PVP desactivado durante la gracia.");
    }
}

