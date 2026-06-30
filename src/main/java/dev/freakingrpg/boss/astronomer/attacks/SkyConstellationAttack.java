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

public final class SkyConstellationAttack extends AbstractBossAttack {

    private static final double DAMAGE = 12.0;
    private final Set<UUID> hit = new HashSet<>();
    private Location constellationCenter;

    public SkyConstellationAttack() {
        super("sky_constellation", Component.text("Draco Constellation", NamedTextColor.AQUA), 50, 35, 25);
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        hit.clear();
        constellationCenter = context.arenaCenter().clone().add(0, 20, 0);
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.AMBIENT_BASALT_DELTAS_ADDITIONS);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        drawDraco(constellationCenter, tick * 0.05);
    }

    @Override
    public void onExecuteStart(BossContext context) {
        context.entity().getWorld().playSound(constellationCenter, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.7f, 1.4f);
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        drawDraco(constellationCenter, tick * 0.08);
        for (int i = 0; i < 5; i++) {
            double angle = (Math.PI * 2 * i) / 5 + tick * 0.1;
            Location sky = constellationCenter.clone().add(Math.cos(angle) * 4, 0, Math.sin(angle) * 4);
            Location ground = sky.clone().subtract(0, sky.getY() - context.arenaCenter().getY(), 0);
            ground.getWorld().spawnParticle(Particle.END_ROD, ground, 6, 0.2, 0.1, 0.2, 0.01);
            for (Player player : context.playersInArena()) {
                if (hit.contains(player.getUniqueId())) {
                    continue;
                }
                if (player.getLocation().distance(ground) <= 2.0) {
                    player.damage(DAMAGE, context.entity());
                    hit.add(player.getUniqueId());
                }
            }
        }
    }

    private void drawDraco(Location center, double offset) {
        for (int i = 0; i < 8; i++) {
            double angle = offset + i * 0.7;
            double radius = 2 + i * 0.35;
            center.getWorld().spawnParticle(
                Particle.GLOW,
                center.getX() + Math.cos(angle) * radius,
                center.getY(),
                center.getZ() + Math.sin(angle) * radius,
                2,
                0,
                0,
                0,
                0
            );
        }
    }
}
