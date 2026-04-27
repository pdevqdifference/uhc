package dev.breiner.uhc.scenario.impl;

import dev.breiner.uhc.game.GameManager;
import dev.breiner.uhc.game.GameState;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public final class HasteyBoysScenario extends BaseScenario implements Listener {
    private final GameManager gameManager;

    public HasteyBoysScenario(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "hasteyboys";
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraft(CraftItemEvent e) {
        if (!isEnabled()) return;
        if (gameManager.getState() != GameState.RUNNING) return;

        if (!(e.getWhoClicked() instanceof Player)) return;

        ItemStack current = e.getCurrentItem();
        if (current == null) return;

        Material type = current.getType();
        if (!isTool(type)) return;

        ItemStack out = current.clone();
        out.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
        out.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

        e.setCurrentItem(out);
    }

    private boolean isTool(Material m) {
        switch (m) {
            case WOOD_AXE:
            case STONE_AXE:
            case IRON_AXE:
            case GOLD_AXE:
            case DIAMOND_AXE:
            case WOOD_PICKAXE:
            case STONE_PICKAXE:
            case IRON_PICKAXE:
            case GOLD_PICKAXE:
            case DIAMOND_PICKAXE:
            case WOOD_SPADE:
            case STONE_SPADE:
            case IRON_SPADE:
            case GOLD_SPADE:
            case DIAMOND_SPADE:
                return true;
            default:
                return false;
        }
    }
}
