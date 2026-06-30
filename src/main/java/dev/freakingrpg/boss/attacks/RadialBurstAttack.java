package dev.freakingrpg.boss.attacks;

import dev.freakingrpg.boss.AbstractBossAttack;
import dev.freakingrpg.boss.BossContext;
import dev.freakingrpg.boss.TelegraphHelper;
import dev.freakingrpg.presentation.BossAnimator;
import dev.freakingrpg.vfx.effects.GroundCrackTelegraph;
import dev.freakingrpg.vfx.effects.RockShockwaveEffect;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class RadialBurstAttack extends AbstractBossAttack {

    private static final double MAX_RADIUS = 8.0;
    private static final double DAMAGE = 8.0;

    private final Set<UUID> hitPlayers = new HashSet<>();
    private double activeRadius;
    private int lastWaveSegment;

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
        lastWaveSegment = 0;
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.BLOCK_BEACON_POWER_SELECT);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        LivingEntity entity = context.entity();
        double progress = tick / (double) telegraphTicks();
        double radius = 2.0 + progress * (MAX_RADIUS * 0.45);
        GroundCrackTelegraph.spawnRing(context.plugin(), entity.getLocation(), radius, 18);
        if (tick % 12 == 0) {
            TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.BLOCK_BEACON_POWER_SELECT);
        }
    }

    @Override
    public void onExecuteStart(BossContext context) {
        context.entity().getWorld().playSound(context.entity().getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.8f, 1.2f);
        context.plugin().presentation().bosses().play(
            context.entity(),
            "shockwave_cast",
            BossAnimator.AnimationMode.SINGLE
        );
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        LivingEntity entity = context.entity();
        Location center = entity.getLocation();
        activeRadius = (tick + 1) / (double) executeTicks() * MAX_RADIUS;

        int waveSegment = (int) (activeRadius * 3);
        if (waveSegment != lastWaveSegment) {
            lastWaveSegment = waveSegment;
            RockShockwaveEffect.spawnPulse(context.plugin(), center, activeRadius, 22);
            center.getWorld().playSound(center, Sound.BLOCK_DEEPSLATE_BREAK, 0.9f, 0.6f + (float) (activeRadius / MAX_RADIUS) * 0.4f);
        }

        for (Player player : context.playersInArena()) {
            if (hitPlayers.contains(player.getUniqueId())) {
                continue;
            }
            double distance = player.getLocation().distance(center);
            if (distance <= activeRadius && distance >= activeRadius - 1.8) {
                player.damage(DAMAGE, entity);
                hitPlayers.add(player.getUniqueId());
            }
        }
    }
}
