package dev.freakingrpg.boss.astronomer;

import dev.freakingrpg.FreakingRpgPlugin;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class GravityField {

    private final FreakingRpgPlugin plugin;
    private GravityAlignment alignment = GravityAlignment.NORMAL;
    private int transitionTicksRemaining;
    private GravityAlignment targetAlignment = GravityAlignment.NORMAL;
    private int telegraphTicksRemaining;

    public GravityField(FreakingRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public GravityAlignment alignment() {
        return alignment;
    }

    public void setImmediate(GravityAlignment alignment) {
        this.alignment = alignment;
        this.targetAlignment = alignment;
        this.transitionTicksRemaining = 0;
        this.telegraphTicksRemaining = 0;
    }

    public void scheduleShift(GravityAlignment target, int telegraphTicks) {
        this.targetAlignment = target;
        this.telegraphTicksRemaining = telegraphTicks;
    }

    public void tick(Collection<Player> players) {
        if (telegraphTicksRemaining > 0) {
            telegraphTicksRemaining--;
            if (telegraphTicksRemaining == 0) {
                transitionTicksRemaining = 20;
            }
            for (Player player : players) {
                player.sendActionBar(Component.text(targetAlignment.warning(), NamedTextColor.AQUA));
            }
            return;
        }

        if (transitionTicksRemaining > 0) {
            transitionTicksRemaining--;
            if (transitionTicksRemaining == 0) {
                alignment = targetAlignment;
            }
        }

        Vector pull = alignment.acceleration();
        for (Player player : players) {
            if (player.isDead() || !player.isOnline()) {
                continue;
            }
            player.setVelocity(player.getVelocity().add(pull));
        }
    }

    public boolean isTelegraphing() {
        return telegraphTicksRemaining > 0;
    }
}
