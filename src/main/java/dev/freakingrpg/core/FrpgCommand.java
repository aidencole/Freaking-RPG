package dev.freakingrpg.core;

import dev.freakingrpg.FreakingRpgPlugin;
import dev.freakingrpg.boss.BossDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class FrpgCommand implements CommandExecutor, TabCompleter {

    private final FreakingRpgPlugin plugin;

    public FrpgCommand(FreakingRpgPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            return handleReload(sender);
        }

        if (args[0].equalsIgnoreCase("boss")) {
            return handleBoss(sender, args);
        }

        sender.sendMessage(plugin.brandedMessage("Unknown subcommand. Use /frpg help."));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.brandedMessage("Commands:"));
        sender.sendMessage(plugin.brandedMessage("/frpg boss list"));
        sender.sendMessage(plugin.brandedMessage("/frpg boss spawn <id>  (astronomer teleports you to frpg_observatory)"));
        sender.sendMessage(plugin.brandedMessage("/frpg boss stop"));
        sender.sendMessage(plugin.brandedMessage("/frpg reload"));
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("freakingrpg.admin")) {
            sender.sendMessage(plugin.brandedMessage("You do not have permission to reload."));
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(plugin.brandedMessage("Configuration reloaded."));
        return true;
    }

    private boolean handleBoss(CommandSender sender, String[] args) {
        if (!sender.hasPermission("freakingrpg.boss")) {
            sender.sendMessage(plugin.brandedMessage("You do not have permission to manage bosses."));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.brandedMessage("Use /frpg boss list|spawn|stop."));
            return true;
        }

        return switch (args[1].toLowerCase(Locale.ROOT)) {
            case "list" -> handleBossList(sender);
            case "spawn" -> handleBossSpawn(sender, args);
            case "stop" -> handleBossStop(sender);
            default -> {
                sender.sendMessage(plugin.brandedMessage("Use /frpg boss list|spawn|stop."));
                yield true;
            }
        };
    }

    private boolean handleBossList(CommandSender sender) {
        var bosses = plugin.bossManager().registry().all();
        sender.sendMessage(plugin.brandedMessage("Available bosses:"));
        for (BossDefinition definition : bosses) {
            sender.sendMessage(plugin.brandedMessage("- " + definition.id() + " (" + definition.maxHealth() + " HP)"));
        }
        return true;
    }

    private boolean handleBossSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.brandedMessage("Only players can spawn bosses."));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(plugin.brandedMessage("Use /frpg boss spawn <id>."));
            return true;
        }

        String bossId = args[2].toLowerCase(Locale.ROOT);
        var spawned = plugin.bossManager().spawn(bossId, player.getLocation(), player);
        if (spawned.isEmpty()) {
            sender.sendMessage(plugin.brandedMessage("Unknown boss id or spawn failed: " + bossId));
            return true;
        }

        sender.sendMessage(plugin.brandedMessage(
            "Spawned " + bossId + ". Watch the boss bar for telegraphs. Punish during recovery windows."
        ));
        return true;
    }

    private boolean handleBossStop(CommandSender sender) {
        int count = plugin.bossManager().activeInstances().size();
        plugin.bossManager().stopAll();
        sender.sendMessage(plugin.brandedMessage("Stopped " + count + " active boss fight(s)."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(List.of("help", "boss", "reload"), args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("boss")) {
            return filter(List.of("list", "spawn", "stop"), args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("boss") && args[1].equalsIgnoreCase("spawn")) {
            List<String> ids = new ArrayList<>();
            plugin.bossManager().registry().all().forEach(definition -> ids.add(definition.id()));
            return filter(ids, args[2]);
        }

        return List.of();
    }

    private static List<String> filter(List<String> options, String prefix) {
        return options.stream()
            .filter(option -> option.startsWith(prefix.toLowerCase(Locale.ROOT)))
            .toList();
    }
}
