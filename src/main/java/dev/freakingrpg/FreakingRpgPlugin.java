package dev.freakingrpg;

import dev.freakingrpg.boss.BossKeys;
import dev.freakingrpg.boss.BossListener;
import dev.freakingrpg.boss.BossManager;
import dev.freakingrpg.boss.BossRegistry;
import dev.freakingrpg.core.FrpgCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class FreakingRpgPlugin extends JavaPlugin {

    private BossManager bossManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        BossRegistry registry = new BossRegistry();
        BossKeys keys = new BossKeys(this);
        bossManager = new BossManager(this, registry, keys);
        getServer().getPluginManager().registerEvents(new BossListener(bossManager), this);

        var command = getCommand("frpg");
        if (command != null) {
            var executor = new FrpgCommand(this);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        } else {
            getLogger().severe("Command /frpg is missing from plugin.yml.");
        }

        getLogger().info("Freaking RPG enabled.");
    }

    @Override
    public void onDisable() {
        if (bossManager != null) {
            bossManager.shutdown();
        }
        getLogger().info("Freaking RPG disabled.");
    }

    public BossManager bossManager() {
        return bossManager;
    }

    public Component brandedMessage(String message) {
        return Component.text("[Freaking RPG] ", NamedTextColor.GOLD)
            .append(Component.text(message, NamedTextColor.YELLOW));
    }
}
