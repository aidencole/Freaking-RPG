package dev.freakingrpg.boss;

import java.util.Collection;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class BossBarController {

    private final BossBar bossBar;

    public BossBarController(Component title, BossBar.Color color) {
        this.bossBar = BossBar.bossBar(title, 1.0f, color, BossBar.Overlay.PROGRESS);
    }

    public void show(Collection<Player> players) {
        for (Player player : players) {
            player.showBossBar(bossBar);
        }
    }

    public void hide(Collection<Player> players) {
        for (Player player : players) {
            player.hideBossBar(bossBar);
        }
    }

    public void setProgress(float progress) {
        bossBar.progress(Math.clamp(progress, 0.0f, 1.0f));
    }

    public void setTitle(Component title) {
        bossBar.name(title);
    }

    public void setColor(BossBar.Color color) {
        bossBar.color(color);
    }

    public BossBar bossBar() {
        return bossBar;
    }
}
