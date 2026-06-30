package dev.freakingrpg;

import dev.freakingrpg.boss.BossKeys;
import dev.freakingrpg.boss.BossListener;
import dev.freakingrpg.boss.BossManager;
import dev.freakingrpg.boss.BossRegistry;
import dev.freakingrpg.core.FrpgCommand;
import dev.freakingrpg.presentation.PresentationServices;
import dev.freakingrpg.boss.astronomer.AstronomerArenaService;
import dev.freakingrpg.vfx.VfxRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class FreakingRpgPlugin extends JavaPlugin {

    private BossManager bossManager;
    private VfxRunner vfxRunner;
    private PresentationServices presentation;
    private AstronomerArenaService astronomerArenaService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        vfxRunner = new VfxRunner(this);
        presentation = new PresentationServices(this);
        astronomerArenaService = new AstronomerArenaService(this);

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

        getLogger().info("Freaking RPG enabled (observatory chunk sync + arena floor). Version: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        if (bossManager != null) {
            bossManager.shutdown();
        }
        if (vfxRunner != null) {
            vfxRunner.shutdown();
        }
        getLogger().info("Freaking RPG disabled.");
    }

    public BossManager bossManager() {
        return bossManager;
    }

    public VfxRunner vfxRunner() {
        return vfxRunner;
    }

    public PresentationServices presentation() {
        return presentation;
    }

    public AstronomerArenaService astronomerArenaService() {
        return astronomerArenaService;
    }

    public Component brandedMessage(String message) {
        return Component.text("[Freaking RPG] ", NamedTextColor.GOLD)
            .append(Component.text(message, NamedTextColor.YELLOW));
    }
}
