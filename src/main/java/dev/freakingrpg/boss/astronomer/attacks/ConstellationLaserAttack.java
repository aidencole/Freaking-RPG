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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class ConstellationLaserAttack extends AbstractBossAttack {

    private static final double DAMAGE = 14.0;
    private Location origin;
    private Vector direction;
    private final Set<UUID> hit = new HashSet<>();

    public ConstellationLaserAttack() {
        super("constellation_laser", Component.text("Constellation Laser", NamedTextColor.LIGHT_PURPLE), 50, 16, 20);
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        hit.clear();
        LivingEntity entity = context.entity();
        Player target = context.nearestPlayer();
        origin = entity.getLocation().clone().add(0, 18, 0);
        if (target != null) {
            direction = target.getLocation().toVector().subtract(origin.toVector()).normalize();
        } else {
            direction = entity.getLocation().getDirection().setY(-0.4).normalize();
        }
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.BLOCK_AMETHYST_BLOCK_CHIME);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        TelegraphHelper.line(origin, direction, 24, Particle.END_ROD, 20);
        TelegraphHelper.line(origin, direction, 24, Particle.WAX_ON, 12);
    }

    @Override
    public void onExecuteStart(BossContext context) {
        context.entity().getWorld().playSound(origin, Sound.ENTITY_GUARDIAN_ATTACK, 1.0f, 0.6f);
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        for (int step = 0; step < 24; step++) {
            Location point = origin.clone().add(direction.clone().multiply(step));
            point.getWorld().spawnParticle(Particle.END_ROD, point, 2, 0.1, 0.1, 0.1, 0);
            for (Player player : context.playersInArena()) {
                if (hit.contains(player.getUniqueId())) {
                    continue;
                }
                if (player.getLocation().distance(point) <= 1.4) {
                    player.damage(DAMAGE, context.entity());
                    hit.add(player.getUniqueId());
                }
            }
        }
    }
}
