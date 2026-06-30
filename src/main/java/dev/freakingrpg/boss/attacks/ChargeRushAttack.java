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
import org.bukkit.util.Vector;

public final class ChargeRushAttack extends AbstractBossAttack {

    private static final double DAMAGE = 9.0;
    private static final double HIT_WIDTH = 2.2;

    private Vector chargeDirection;
    private final Set<UUID> hitPlayers = new HashSet<>();

    public ChargeRushAttack() {
        super(
            "charge_rush",
            Component.text("Bull Rush!", NamedTextColor.GOLD),
            40,
            30,
            24
        );
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        hitPlayers.clear();
        Player target = context.nearestPlayer();
        LivingEntity entity = context.entity();
        if (target == null) {
            chargeDirection = entity.getLocation().getDirection().setY(0).normalize();
        } else {
            chargeDirection = target.getLocation().toVector()
                .subtract(entity.getLocation().toVector())
                .setY(0)
                .normalize();
        }
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.ENTITY_RAVAGER_CELEBRATE);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        Location start = context.entity().getLocation();
        double length = 8.0 + tick * 0.1;
        TelegraphHelper.line(start, chargeDirection, length, Particle.FLAME, 18);
        TelegraphHelper.warningRing(start, 2.0, 12);
    }

    @Override
    public void onExecuteStart(BossContext context) {
        context.entity().getWorld().playSound(context.entity().getLocation(), Sound.ENTITY_RAVAGER_ATTACK, 1.0f, 0.8f);
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        LivingEntity entity = context.entity();
        entity.setVelocity(chargeDirection.clone().multiply(0.85));

        Location bossLoc = entity.getLocation();
        for (Player player : context.playersInArena()) {
            if (hitPlayers.contains(player.getUniqueId())) {
                continue;
            }
            if (distanceToLine(player.getLocation(), bossLoc, chargeDirection) <= HIT_WIDTH) {
                player.damage(DAMAGE, entity);
                player.setVelocity(chargeDirection.clone().multiply(0.6).setY(0.25));
                hitPlayers.add(player.getUniqueId());
            }
        }

        bossLoc.getWorld().spawnParticle(Particle.CLOUD, bossLoc.add(0, 0.5, 0), 4, 0.2, 0.1, 0.2, 0.01);
    }

    @Override
    public void onRecoverStart(BossContext context) {
        context.entity().setVelocity(new Vector(0, 0, 0));
    }

    private static double distanceToLine(Location point, Location lineStart, Vector direction) {
        Vector ap = point.toVector().subtract(lineStart.toVector());
        Vector ab = direction.clone().normalize();
        double projection = ap.dot(ab);
        Vector closest = lineStart.toVector().add(ab.multiply(projection));
        return point.toVector().distance(closest);
    }
}
