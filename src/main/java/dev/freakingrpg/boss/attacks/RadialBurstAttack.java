package dev.freakingrpg.boss.attacks;

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

public final class RadialBurstAttack extends AbstractBossAttack {

    private static final double MAX_RADIUS = 8.0;
    private static final double DAMAGE = 8.0;

    private final Set<UUID> hitPlayers = new HashSet<>();
    private double activeRadius;

    public RadialBurstAttack() {
        super(
            "radial_burst",
            Component.text("Shockwave!", NamedTextColor.AQUA),
            45,
            20,
            28
        );
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        hitPlayers.clear();
        activeRadius = 0;
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.BLOCK_BEACON_POWER_SELECT);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        LivingEntity entity = context.entity();
        TelegraphHelper.spiral(entity.getLocation(), 4.5, 3, 10, tick);
        if (tick % 12 == 0) {
            TelegraphHelper.warningRing(entity.getLocation(), 2.5 + tick * 0.05, 16);
        }
    }

    @Override
    public void onExecuteStart(BossContext context) {
        context.entity().getWorld().playSound(context.entity().getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.8f, 1.2f);
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        LivingEntity entity = context.entity();
        Location center = entity.getLocation();
        activeRadius = (tick + 1) / (double) executeTicks() * MAX_RADIUS;

        TelegraphHelper.dangerRing(center, activeRadius, 32);
        center.getWorld().spawnParticle(Particle.SONIC_BOOM, center.clone().add(0, 1, 0), 1, 0, 0, 0, 0);

        for (Player player : context.playersInArena()) {
            if (hitPlayers.contains(player.getUniqueId())) {
                continue;
            }
            double distance = player.getLocation().distance(center);
            if (distance <= activeRadius && distance >= activeRadius - 1.5) {
                player.damage(DAMAGE, entity);
                hitPlayers.add(player.getUniqueId());
            }
        }
    }
}
