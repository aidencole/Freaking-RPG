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

public final class GreatswordArcAttack extends AbstractBossAttack {

    private static final double RANGE = 7.0;
    private static final double ARC = Math.toRadians(110);
    private static final double DAMAGE = 16.0;
    private final Set<UUID> hit = new HashSet<>();

    public GreatswordArcAttack() {
        super("greatsword_arc", Component.text("Greatsword Arc", NamedTextColor.GRAY), 35, 10, 22);
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        hit.clear();
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.ITEM_TRIDENT_THUNDER);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        LivingEntity entity = context.entity();
        Vector facing = entity.getLocation().getDirection().setY(0).normalize();
        TelegraphHelper.line(entity.getLocation(), facing, RANGE, Particle.SWEEP_ATTACK, 10);
    }

    @Override
    public void onExecuteStart(BossContext context) {
        context.entity().getWorld().playSound(context.entity().getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.2f, 0.5f);
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        LivingEntity entity = context.entity();
        Location origin = entity.getLocation();
        Vector facing = origin.getDirection().setY(0).normalize();
        double baseAngle = Math.atan2(facing.getZ(), facing.getX());

        for (Player player : context.playersInArena()) {
            if (hit.contains(player.getUniqueId())) {
                continue;
            }
            Vector offset = player.getLocation().toVector().subtract(origin.toVector()).setY(0);
            double distance = offset.length();
            if (distance > RANGE || distance < 1.5) {
                continue;
            }
            double angle = Math.atan2(offset.getZ(), offset.getX());
            double delta = Math.abs(normalize(angle - baseAngle));
            if (delta <= ARC / 2) {
                player.damage(DAMAGE, entity);
                hit.add(player.getUniqueId());
            }
        }
    }

    private static double normalize(double angle) {
        while (angle > Math.PI) {
            angle -= Math.PI * 2;
        }
        while (angle < -Math.PI) {
            angle += Math.PI * 2;
        }
        return angle;
    }
}
