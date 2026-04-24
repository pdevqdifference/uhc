package dev.pdevqdifference.uhc.command;

import dev.pdevqdifference.uhc.UhcPlugin;
import dev.pdevqdifference.uhc.game.GameManager;
import dev.pdevqdifference.uhc.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class UhcCommand implements CommandExecutor {
    private final UhcPlugin plugin;
    private final GameManager gameManager;

    public UhcCommand(UhcPlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc <start|stop|status|reload>");
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "start":
                if (!(sender instanceof Player) && sender.getName() == null) {
                    // ignore
                }
                if (gameManager.getState() == GameState.RUNNING) {
                    sender.sendMessage(ChatColor.RED + "Ya hay una partida en curso.");
                    return true;
                }
                gameManager.start();
                return true;

            case "stop":
                gameManager.stop();
                return true;

            case "status":
                sender.sendMessage(ChatColor.AQUA + "[UHC] " + gameManager.statusLine());
                return true;

            case "reload":
                plugin.reloadPluginConfig();
                sender.sendMessage(ChatColor.GREEN + "[UHC] Config recargada.");
                return true;

            default:
                sender.sendMessage(ChatColor.YELLOW + "Uso: /uhc <start|stop|status|reload>");
                return true;
        }
    }
}

