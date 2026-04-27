package dev.breiner.uhc.listener;

import dev.breiner.uhc.game.GameManager;
import dev.breiner.uhc.game.GameState;
import dev.breiner.uhc.host.HostManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class LateJoinListener implements Listener {
    private final GameManager gameManager;
    private final HostManager hostManager;

    public LateJoinListener(GameManager gameManager, HostManager hostManager) {
        this.gameManager = gameManager;
        this.hostManager = hostManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (gameManager.getState() != GameState.RUNNING) return;

        if (hostManager.isLocked()) {
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(ChatColor.RED + "[UHC] La partida está bloqueada. Entraste como espectador.");
        }
    }
}
