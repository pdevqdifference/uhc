package dev.varga.uhc.listener;

import dev.varga.uhc.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public final class GameRulesListener implements Listener {
    private final GameManager gameManager;

    public GameRulesListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRegain(EntityRegainHealthEvent e) {
        if (!gameManager.isDisableNaturalRegen()) return;
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            e.setCancelled(true);
        }
    }
}

