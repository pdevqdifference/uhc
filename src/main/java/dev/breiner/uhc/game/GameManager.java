package dev.breiner.uhc.game;

import dev.breiner.uhc.UhcPlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class GameManager {
    private final UhcPlugin plugin;

    private GameState state = GameState.LOBBY;
    private long gameStartMillis = 0L;

    private int gracePeriodSeconds = 120;
    private int scatterRadius = 1000;
    private boolean startHeal = true;
    private boolean disableNaturalRegen = true;

    private boolean borderEnabled = true;
    private double borderInitial = 2000;
    private double borderFinal = 200;
    private int borderShrinkSeconds = 900;

    private BukkitTask borderTask;
    private BukkitTask graceTask;

    private final Set<UUID> alive = new HashSet<>();

    public GameManager(UhcPlugin plugin) {
        this.plugin = plugin;
        reloadFromConfig();
    }

    public void reloadFromConfig() {
        this.gracePeriodSeconds = plugin.getConfig().getInt("grace-period-seconds", 120);
        this.scatterRadius = plugin.getConfig().getInt("scatter-radius", 1000);
        this.startHeal = plugin.getConfig().getBoolean("start-heal", true);
        this.disableNaturalRegen = plugin.getConfig().getBoolean("disable-natural-regen", true);

        this.borderEnabled = plugin.getConfig().getBoolean("world-border.enabled", true);
        this.borderInitial = plugin.getConfig().getDouble("world-border.initial-size", 2000);
        this.borderFinal = plugin.getConfig().getDouble("world-border.final-size", 200);
        this.borderShrinkSeconds = plugin.getConfig().getInt("world-border.shrink-seconds", 900);
    }

    public GameState getState() {
        return state;
    }

    public boolean isGraceActive() {
        if (state != GameState.RUNNING) return false;
        if (gracePeriodSeconds <= 0) return false;
        long elapsed = System.currentTimeMillis() - gameStartMillis;
        return elapsed < gracePeriodSeconds * 1000L;
    }

    public boolean isDisableNaturalRegen() {
        return disableNaturalRegen;
    }

    public boolean isAlive(UUID uuid) {
        return alive.contains(uuid);
    }

    public Set<UUID> getAliveSnapshot() {
        return new HashSet<>(alive);
    }

    public void revive(Player player) {
        if (player == null) return;
        alive.add(player.getUniqueId());
        preparePlayer(player);
    }

    public void start() {
        if (state == GameState.RUNNING) return;

        World world = Bukkit.getWorlds().get(0);
        world.setGameRuleValue("naturalRegeneration", disableNaturalRegen ? "false" : "true");

        alive.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            alive.add(p.getUniqueId());
            preparePlayer(p);
        }

        if (startHeal) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.setHealth(p.getMaxHealth());
                p.setFoodLevel(20);
                p.setSaturation(20f);
            }
        }

        scatterAll(world);

        setupBorder(world);

        this.state = GameState.RUNNING;
        this.gameStartMillis = System.currentTimeMillis();

        Bukkit.broadcastMessage(ChatColor.GREEN + "[UHC] La partida ha comenzado. Gracia: " + gracePeriodSeconds + "s");
        scheduleGraceEndMessage();
    }

    private void preparePlayer(Player p) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.setGameMode(GameMode.SURVIVAL);
        p.setAllowFlight(false);
        p.setFlying(false);
        p.setFireTicks(0);
        p.setFallDistance(0f);
        p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
    }

    private void scatterAll(World world) {
        Location center = world.getSpawnLocation();
        Random random = new Random();
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players, random);

        for (Player p : players) {
            Location loc = findSafeLocation(world, center, scatterRadius, random);
            p.teleport(loc);
        }
    }

    private Location findSafeLocation(World world, Location center, int radius, Random random) {
        int attempts = 0;
        while (attempts++ < 40) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            int x = center.getBlockX() + dx;
            int z = center.getBlockZ() + dz;
            int y = world.getHighestBlockYAt(x, z);
            if (y <= 0) continue;

            Location candidate = new Location(world, x + 0.5, y + 1.0, z + 0.5);
            Material feet = candidate.getBlock().getType();
            Material below = candidate.clone().subtract(0, 1, 0).getBlock().getType();
            if (feet == Material.AIR && below != Material.AIR && below != Material.LAVA && below != Material.STATIONARY_LAVA) {
                return candidate;
            }
        }

        // Fallback: spawn
        return world.getSpawnLocation().add(0.5, 1.0, 0.5);
    }

    private void setupBorder(World world) {
        if (!borderEnabled) return;

        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(world.getSpawnLocation());
        wb.setSize(borderInitial);

        if (borderTask != null) borderTask.cancel();
        borderTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            wb.setSize(borderFinal, borderShrinkSeconds);
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[UHC] El borde está encogiéndose.");
        }, 20L); // 1s después del start
    }

    private void scheduleGraceEndMessage() {
        if (graceTask != null) graceTask.cancel();
        if (gracePeriodSeconds <= 0) return;

        graceTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (state != GameState.RUNNING) return;
            Bukkit.broadcastMessage(ChatColor.RED + "[UHC] Fin de gracia. ¡PVP activado!");
        }, gracePeriodSeconds * 20L);
    }

    public void handleDeath(Player dead) {
        alive.remove(dead.getUniqueId());

        dead.setGameMode(GameMode.SPECTATOR);
        dead.setAllowFlight(true);
        dead.setFlying(true);

        checkWinCondition();
    }

    private void checkWinCondition() {
        if (state != GameState.RUNNING) return;

        int aliveOnline = 0;
        Player last = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (alive.contains(p.getUniqueId())) {
                aliveOnline++;
                last = p;
            }
        }

        if (aliveOnline <= 1) {
            if (last != null) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "[UHC] Ganador: " + ChatColor.YELLOW + last.getName());
            } else {
                Bukkit.broadcastMessage(ChatColor.GOLD + "[UHC] La partida terminó (sin ganador).");
            }
            stop();
        }
    }

    public void stop() {
        if (state == GameState.LOBBY) return;
        forceStop();
        Bukkit.broadcastMessage(ChatColor.RED + "[UHC] Partida finalizada.");
    }

    public void forceStop() {
        if (borderTask != null) {
            borderTask.cancel();
            borderTask = null;
        }
        if (graceTask != null) {
            graceTask.cancel();
            graceTask = null;
        }

        this.state = GameState.ENDED;

        // Reset border to something reasonable
        World world = Bukkit.getWorlds().get(0);
        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(world.getSpawnLocation());
        wb.setSize(60000000D);
    }

    public String statusLine() {
        if (state == GameState.LOBBY) return ChatColor.YELLOW + "Estado: LOBBY";
        if (state == GameState.ENDED) return ChatColor.RED + "Estado: ENDED";

        long elapsedSec = Math.max(0, (System.currentTimeMillis() - gameStartMillis) / 1000L);
        return ChatColor.GREEN + "Estado: RUNNING " + ChatColor.GRAY + "(t=" + elapsedSec + "s, gracia=" + (isGraceActive() ? "ON" : "OFF") + ")";
    }
}

