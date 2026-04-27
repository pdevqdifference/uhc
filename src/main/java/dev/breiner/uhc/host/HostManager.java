package dev.breiner.uhc.host;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class HostManager {
    private final Set<UUID> frozen = new HashSet<>();
    private boolean locked;

    public boolean isFrozen(UUID uuid) {
        return frozen.contains(uuid);
    }

    public Set<UUID> getFrozenSnapshot() {
        return Collections.unmodifiableSet(new HashSet<>(frozen));
    }

    public void freeze(Player p) {
        if (p == null) return;
        frozen.add(p.getUniqueId());
    }

    public void unfreeze(Player p) {
        if (p == null) return;
        frozen.remove(p.getUniqueId());
    }

    public void freezeAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            freeze(p);
        }
    }

    public void unfreezeAll() {
        frozen.clear();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
