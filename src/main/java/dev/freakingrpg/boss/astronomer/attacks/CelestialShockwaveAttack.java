package dev.freakingrpg.boss.astronomer.attacks;

import dev.freakingrpg.boss.AbstractBossAttack;
import dev.freakingrpg.boss.BossContext;
import dev.freakingrpg.boss.TelegraphHelper;
import dev.freakingrpg.vfx.effects.RockShockwaveEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class CelestialShockwaveAttack extends AbstractBossAttack {

    private static final double MAX_RADIUS = 10.0;
    private static final double DAMAGE = 10.0;
    private double activeRadius;
    private int lastSegment;

    public CelestialShockwaveAttack() {
        super("celestial_shockwave", Component.text("Celestial Shockwave", NamedTextColor.YELLOW), 40, 18, 24);
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        activeRadius = 0;
        lastSegment = 0;
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.BLOCK_BEACON_AMBIENT);
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        LivingEntity entity = context.entity();
        Location center = entity.getLocation();
        activeRadius = (tick + 1) / (double) executeTicks() * MAX_RADIUS;
        int segment = (int) (activeRadius * 2);
        if (segment != lastSegment) {
            lastSegment = segment;
            RockShockwaveEffect.spawnPulse(context.plugin(), center, activeRadius, 18);
        }

        for (Player player : context.playersInArena()) {
            if (player.getLocation().distance(center) <= activeRadius && player.getLocation().distance(center) >= activeRadius - 1.5) {
                player.damage(DAMAGE, entity);
            }
        }
        center.getWorld().spawnParticle(Particle.GLOW, center, 8, activeRadius * 0.2, 0.2, activeRadius * 0.2, 0.01);
    }
}
