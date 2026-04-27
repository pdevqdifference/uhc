package dev.breiner.uhc.command;

import dev.breiner.uhc.UhcPlugin;
import dev.breiner.uhc.game.GameManager;
import dev.breiner.uhc.game.GameState;
import dev.breiner.uhc.host.HostManager;
import dev.breiner.uhc.scenario.Scenario;
import dev.breiner.uhc.scenario.ScenarioManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class UhcCommand implements CommandExecutor, TabCompleter {
    private final UhcPlugin plugin;
    private final GameManager gameManager;
    private final ScenarioManager scenarioManager;
    private final HostManager hostManager;

    public UhcCommand(UhcPlugin plugin, GameManager gameManager, ScenarioManager scenarioManager, HostManager hostManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
        this.hostManager = hostManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "start":
                if (!sender.hasPermission("uhc.admin")) {
                    sender.sendMessage(ChatColor.RED + "[UHC] No tienes permiso.");
                    return true;
                }
                if (gameManager.getState() == GameState.RUNNING) {
                    sender.sendMessage(ChatColor.RED + "Ya hay una partida en curso.");
                    return true;
                }
                gameManager.start();
                return true;

            case "stop":
                if (!sender.hasPermission("uhc.admin")) {
                    sender.sendMessage(ChatColor.RED + "[UHC] No tienes permiso.");
                    return true;
                }
                gameManager.stop();
                return true;

            case "status":
                sender.sendMessage(ChatColor.AQUA + "[UHC] " + gameManager.statusLine());
                return true;

            case "reload":
                if (!sender.hasPermission("uhc.admin")) {
                    sender.sendMessage(ChatColor.RED + "[UHC] No tienes permiso.");
                    return true;
                }
                plugin.reloadPluginConfig();
                sender.sendMessage(ChatColor.GREEN + "[UHC] Config recargada.");
                return true;

            case "help":
                sendHelp(sender);
                return true;

            case "scenario":
            case "sc":
                return handleScenario(sender, args);

            case "host":
                return handleHost(sender, args);

            case "config":
                return handleConfig(sender, args);

            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc <start|stop|status|reload|scenario|host|config|help>");
        sender.sendMessage(ChatColor.GRAY + "/uhc scenario list");
        sender.sendMessage(ChatColor.GRAY + "/uhc scenario on <nombre> | off <nombre>");
        sender.sendMessage(ChatColor.GRAY + "/uhc host freeze <player> | unfreeze <player> | freezeall | unfreezeall");
        sender.sendMessage(ChatColor.GRAY + "/uhc host revive <player> | invsee <player> | tpall | tpalive | lock | unlock");
        sender.sendMessage(ChatColor.GRAY + "/uhc config set <path> <valor>");
    }

    private boolean handleScenario(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc scenario <list|on|off> ...");
            return true;
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        switch (action) {
            case "list": {
                StringBuilder sb = new StringBuilder(ChatColor.AQUA + "[UHC] Scenarios: ");
                boolean first = true;
                for (Scenario sc : scenarioManager.getAll()) {
                    if (!first) sb.append(ChatColor.GRAY).append(", ");
                    first = false;
                    sb.append(sc.isEnabled() ? ChatColor.GREEN : ChatColor.RED).append(sc.getName());
                }
                sender.sendMessage(sb.toString());
                return true;
            }
            case "on":
            case "enable": {
                if (!sender.hasPermission("uhc.host")) {
                    sender.sendMessage(ChatColor.RED + "[UHC] No tienes permiso.");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc scenario on <nombre>");
                    return true;
                }
                String name = args[2];
                if (!scenarioManager.enable(name)) {
                    sender.sendMessage(ChatColor.RED + "[UHC] Scenario no encontrado: " + name);
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "[UHC] Scenario activado: " + name);
                return true;
            }
            case "off":
            case "disable": {
                if (!sender.hasPermission("uhc.host")) {
                    sender.sendMessage(ChatColor.RED + "[UHC] No tienes permiso.");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc scenario off <nombre>");
                    return true;
                }
                String name = args[2];
                if (!scenarioManager.disable(name)) {
                    sender.sendMessage(ChatColor.RED + "[UHC] Scenario no encontrado: " + name);
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "[UHC] Scenario desactivado: " + name);
                return true;
            }
            default:
                sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc scenario <list|on|off>");
                return true;
        }
    }

    private boolean handleHost(CommandSender sender, String[] args) {
        if (!sender.hasPermission("uhc.host")) {
            sender.sendMessage(ChatColor.RED + "[UHC] No tienes permiso.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc host <freeze|unfreeze|freezeall|unfreezeall|revive|invsee|tpall|tpalive|lock|unlock>");
            return true;
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        switch (action) {
            case "freeze": {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc host freeze <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "[UHC] Jugador no encontrado.");
                    return true;
                }
                hostManager.freeze(target);
                sender.sendMessage(ChatColor.GREEN + "[UHC] Freeze: " + target.getName());
                target.sendMessage(ChatColor.RED + "[UHC] Has sido freezeado por un host.");
                return true;
            }
            case "unfreeze": {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc host unfreeze <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "[UHC] Jugador no encontrado.");
                    return true;
                }
                hostManager.unfreeze(target);
                sender.sendMessage(ChatColor.GREEN + "[UHC] Unfreeze: " + target.getName());
                target.sendMessage(ChatColor.GREEN + "[UHC] Ya no estás freezeado.");
                return true;
            }
            case "freezeall":
                hostManager.freezeAll();
                Bukkit.broadcastMessage(ChatColor.RED + "[UHC] Freeze global activado por un host.");
                return true;

            case "unfreezeall":
                hostManager.unfreezeAll();
                Bukkit.broadcastMessage(ChatColor.GREEN + "[UHC] Freeze global desactivado.");
                return true;

            case "revive": {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc host revive <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "[UHC] Jugador no encontrado.");
                    return true;
                }
                gameManager.revive(target);
                sender.sendMessage(ChatColor.GREEN + "[UHC] Revivido: " + target.getName());
                return true;
            }
            case "invsee": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "[UHC] Solo jugadores.");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc host invsee <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "[UHC] Jugador no encontrado.");
                    return true;
                }
                ((Player) sender).openInventory(target.getInventory());
                return true;
            }
            case "tpall": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "[UHC] Solo jugadores.");
                    return true;
                }
                Player p = (Player) sender;
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.teleport(p.getLocation());
                }
                sender.sendMessage(ChatColor.GREEN + "[UHC] Teleport all.");
                return true;
            }
            case "tpalive": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "[UHC] Solo jugadores.");
                    return true;
                }
                Player p = (Player) sender;
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (gameManager.isAlive(online.getUniqueId())) {
                        online.teleport(p.getLocation());
                    }
                }
                sender.sendMessage(ChatColor.GREEN + "[UHC] Teleport alive.");
                return true;
            }
            case "lock":
                hostManager.setLocked(true);
                Bukkit.setWhitelist(true);
                Bukkit.broadcastMessage(ChatColor.RED + "[UHC] Partida bloqueada por host.");
                return true;

            case "unlock":
                hostManager.setLocked(false);
                Bukkit.setWhitelist(false);
                Bukkit.broadcastMessage(ChatColor.GREEN + "[UHC] Partida desbloqueada.");
                return true;

            default:
                sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc host <freeze|unfreeze|freezeall|unfreezeall|revive|invsee|tpall|tpalive|lock|unlock>");
                return true;
        }
    }

    private boolean handleConfig(CommandSender sender, String[] args) {
        if (!sender.hasPermission("uhc.admin")) {
            sender.sendMessage(ChatColor.RED + "[UHC] No tienes permiso.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc config set <path> <valor>");
            return true;
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        if (!action.equals("set")) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc config set <path> <valor>");
            return true;
        }
        if (args.length < 4) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc config set <path> <valor>");
            return true;
        }

        String path = args[2];
        String raw = args[3];
        Object value = parseValue(raw);
        plugin.getConfig().set(path, value);
        plugin.saveConfig();
        plugin.reloadPluginConfig();
        sender.sendMessage(ChatColor.GREEN + "[UHC] Config set " + path + " = " + String.valueOf(value));
        return true;
    }

    private Object parseValue(String raw) {
        if (raw == null) return "";
        if (raw.equalsIgnoreCase("true")) return true;
        if (raw.equalsIgnoreCase("false")) return false;
        try {
            if (raw.contains(".")) return Double.parseDouble(raw);
            return Integer.parseInt(raw);
        } catch (NumberFormatException ignored) {
            return raw;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterPrefix(Arrays.asList("start", "stop", "status", "reload", "scenario", "host", "config", "help"), args[0]);
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("scenario") || sub.equals("sc")) {
            if (args.length == 2) {
                return filterPrefix(Arrays.asList("list", "on", "off"), args[1]);
            }
            if (args.length == 3 && (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("off"))) {
                List<String> names = new ArrayList<>();
                for (Scenario sc : scenarioManager.getAll()) {
                    names.add(sc.getName());
                }
                return filterPrefix(names, args[2]);
            }
        }

        if (sub.equals("host")) {
            if (args.length == 2) {
                return filterPrefix(Arrays.asList("freeze", "unfreeze", "freezeall", "unfreezeall", "revive", "invsee", "tpall", "tpalive", "lock", "unlock"), args[1]);
            }
            if (args.length == 3 && (args[1].equalsIgnoreCase("freeze") || args[1].equalsIgnoreCase("unfreeze") || args[1].equalsIgnoreCase("revive") || args[1].equalsIgnoreCase("invsee"))) {
                List<String> players = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    players.add(p.getName());
                }
                return filterPrefix(players, args[2]);
            }
        }

        if (sub.equals("config")) {
            if (args.length == 2) {
                return filterPrefix(Collections.singletonList("set"), args[1]);
            }
        }

        return Collections.emptyList();
    }

    private List<String> filterPrefix(List<String> base, String prefixRaw) {
        String prefix = prefixRaw == null ? "" : prefixRaw.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (String s : base) {
            if (s.toLowerCase(Locale.ROOT).startsWith(prefix)) out.add(s);
        }
        return out;
    }
}

