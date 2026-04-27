package dev.breiner.uhc.scenario.impl;

import dev.breiner.uhc.game.GameManager;
import dev.breiner.uhc.game.GameState;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public final class CutCleanScenario extends BaseScenario implements Listener {
    private final GameManager gameManager;

    public CutCleanScenario(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "cutclean";
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (!isEnabled()) return;
        if (gameManager.getState() != GameState.RUNNING) return;

        Player p = e.getPlayer();
        if (p == null) return;

        Material type = e.getBlock().getType();
        Material drop = null;
        if (type == Material.IRON_ORE) drop = Material.IRON_INGOT;
        if (type == Material.GOLD_ORE) drop = Material.GOLD_INGOT;

        if (drop == null) return;

        World world = e.getBlock().getWorld();
        world.dropItemNaturally(e.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(drop, 1));
        e.getBlock().setType(Material.AIR);
        e.setExpToDrop(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent e) {
        if (!isEnabled()) return;
        if (gameManager.getState() != GameState.RUNNING) return;

        Entity entity = e.getEntity();
        if (!(entity instanceof Animals)) return;

        for (Iterator<ItemStack> it = e.getDrops().iterator(); it.hasNext(); ) {
            ItemStack drop = it.next();
            if (drop == null) continue;

            Material cooked = null;
            if (drop.getType() == Material.RAW_BEEF) cooked = Material.COOKED_BEEF;
            if (drop.getType() == Material.PORK) cooked = Material.GRILLED_PORK;
            if (drop.getType() == Material.RAW_CHICKEN) cooked = Material.COOKED_CHICKEN;

            if (cooked != null) {
                it.remove();
                e.getDrops().add(new ItemStack(cooked, drop.getAmount()));
                return;
            }
        }
    }
}
