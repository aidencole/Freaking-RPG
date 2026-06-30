package dev.freakingrpg.boss.astronomer.attacks;

import dev.freakingrpg.boss.AbstractBossAttack;
import dev.freakingrpg.boss.BossContext;
import dev.freakingrpg.boss.TelegraphHelper;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class OrbitalPullAttack extends AbstractBossAttack {

    private static final double PULL_RADIUS = 12.0;
    private static final double DAMAGE = 4.0;
    private Location well;
    private final Set<UUID> hit = new HashSet<>();

    public OrbitalPullAttack() {
        super("orbital_pull", Component.text("Orbital Pull", NamedTextColor.YELLOW), 40, 50, 24);
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        hit.clear();
        Player target = context.nearestPlayer();
        well = target != null ? target.getLocation().clone() : context.entity().getLocation().clone();
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.BLOCK_CONDUIT_ACTIVATE);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        well.getWorld().spawnParticle(Particle.FLAME, well, 12, 0.4, 0.4, 0.4, 0.02);
        TelegraphHelper.warningRing(well, 2.5, 16);
    }

    @Override
    public void onExecuteStart(BossContext context) {
        well.getWorld().playSound(well, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.4f);
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        well.getWorld().spawnParticle(Particle.PORTAL, well, 20, 0.6, 0.6, 0.6, 0.1);
        for (Player player : context.playersInArena()) {
            double distance = player.getLocation().distance(well);
            if (distance > PULL_RADIUS) {
                continue;
            }
            Vector pull = well.toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.22);
            player.setVelocity(player.getVelocity().add(pull));
            if (distance < 2.0 && !hit.contains(player.getUniqueId())) {
                player.damage(DAMAGE, context.entity());
                hit.add(player.getUniqueId());
            }
        }
    }
}
