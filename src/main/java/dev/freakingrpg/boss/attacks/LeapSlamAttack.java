package dev.freakingrpg.boss.attacks;

import dev.freakingrpg.boss.AbstractBossAttack;
import dev.freakingrpg.boss.BossContext;
import dev.freakingrpg.boss.TelegraphHelper;
import dev.freakingrpg.presentation.BossAnimator;
import dev.freakingrpg.vfx.effects.GroundCrackTelegraph;
import dev.freakingrpg.vfx.effects.ImpactCraterEffect;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class LeapSlamAttack extends AbstractBossAttack {

    private static final double SLAM_RADIUS = 5.5;
    private static final double DAMAGE = 12.0;

    private Location slamCenter;
    private boolean launched;

    public LeapSlamAttack() {
        super(
            "leap_slam",
            Component.text("Leap Slam!", NamedTextColor.RED),
            50,
            24,
            30
        );
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        slamCenter = context.entity().getLocation().clone();
        launched = false;
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.BLOCK_NOTE_BLOCK_BASS);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        double progress = tick / (double) telegraphTicks();
        double radius = SLAM_RADIUS * (0.4 + progress * 0.6);
        GroundCrackTelegraph.spawnRing(context.plugin(), slamCenter, radius, 24);
        if (tick % 10 == 0) {
            TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.BLOCK_NOTE_BLOCK_BASS);
        }
    }

    @Override
    public void onExecuteStart(BossContext context) {
        LivingEntity entity = context.entity();
        entity.setVelocity(new Vector(0, 1.1, 0));
        launched = true;
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1.0f, 0.7f);
        context.plugin().presentation().bosses().play(
            entity,
            "leap_slam",
            BossAnimator.AnimationMode.SINGLE
        );
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        LivingEntity entity = context.entity();
        if (launched && tick >= 10 && entity.isOnGround()) {
            impact(context);
            launched = false;
        }
    }

    @Override
    public void onRecoverStart(BossContext context) {
        context.entity().setVelocity(new Vector(0, 0, 0));
    }

    private void impact(BossContext context) {
        Location center = context.entity().getLocation();
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        ImpactCraterEffect.burst(context.plugin(), center, SLAM_RADIUS, 28);

        Set<UUID> hit = new HashSet<>();
        for (Player player : context.playersInArena()) {
            if (player.getLocation().distanceSquared(center) <= SLAM_RADIUS * SLAM_RADIUS) {
                player.damage(DAMAGE, context.entity());
                player.setVelocity(new Vector(0, 0.45, 0));
                hit.add(player.getUniqueId());
            }
        }

        if (!hit.isEmpty()) {
            context.entity().getWorld().playSound(center, Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 0.6f);
        }
    }
}
