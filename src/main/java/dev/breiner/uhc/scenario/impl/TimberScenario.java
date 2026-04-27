package dev.breiner.uhc.scenario.impl;

import dev.breiner.uhc.game.GameManager;
import dev.breiner.uhc.game.GameState;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public final class TimberScenario extends BaseScenario implements Listener {
    private final GameManager gameManager;

    public TimberScenario(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "timber";
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (!isEnabled()) return;
        if (gameManager.getState() != GameState.RUNNING) return;

        Block start = e.getBlock();
        if (!isLog(start.getType())) return;

        World world = start.getWorld();
        Set<String> visited = new HashSet<>();
        ArrayDeque<Block> queue = new ArrayDeque<>();
        queue.add(start);

        int broken = 0;
        while (!queue.isEmpty() && broken < 256) {
            Block b = queue.poll();
            String key = b.getX() + ":" + b.getY() + ":" + b.getZ();
            if (!visited.add(key)) continue;

            if (!isLog(b.getType())) continue;

            world.dropItemNaturally(b.getLocation().add(0.5, 0.5, 0.5), new ItemStack(b.getType(), 1));
            b.setType(Material.AIR);
            broken++;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        queue.add(b.getRelative(dx, dy, dz));
                    }
                }
            }
        }

        e.setCancelled(true);
    }

    private boolean isLog(Material m) {
        return m == Material.LOG || m == Material.LOG_2;
    }
}
