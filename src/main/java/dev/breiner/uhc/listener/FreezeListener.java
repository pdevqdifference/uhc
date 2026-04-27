package dev.breiner.uhc.listener;

import dev.breiner.uhc.host.HostManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public final class FreezeListener implements Listener {
    private final HostManager hostManager;

    public FreezeListener(HostManager hostManager) {
        this.hostManager = hostManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!hostManager.isFrozen(p.getUniqueId())) return;

        if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ()) {
            e.setTo(e.getFrom());
        }
    }
}
