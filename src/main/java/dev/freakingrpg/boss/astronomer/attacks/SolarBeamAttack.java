package dev.freakingrpg.boss.astronomer.attacks;

import dev.freakingrpg.boss.AbstractBossAttack;
import dev.freakingrpg.boss.BossContext;
import dev.freakingrpg.boss.astronomer.AstronomerEncounter;
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

public final class SolarBeamAttack extends AbstractBossAttack {

    private static final double DAMAGE = 18.0;
    private final Set<UUID> hit = new HashSet<>();
    private Vector beamDirection;
    private Location beamOrigin;

    public SolarBeamAttack() {
        super("solar_beam", Component.text("Solar Beam", NamedTextColor.GOLD), 45, 40, 20);
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        hit.clear();
        beamOrigin = context.arenaCenter().clone().add(0, 1, 0);
        beamDirection = context.entity().getLocation().getDirection().setY(0).normalize();
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.BLOCK_BEACON_POWER_SELECT);
        AstronomerEncounter.from(context).ifPresent(encounter -> {
            encounter.rings().setRotationBoost(1.5);
            encounter.rings().rings().getFirst().setLocked(false);
        });
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        TelegraphHelper.line(beamOrigin, beamDirection, context.arenaRadius(), Particle.FLAME, 24);
    }

    @Override
    public void onExecuteStart(BossContext context) {
        context.entity().getWorld().playSound(beamOrigin, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.4f);
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        for (int step = 0; step < context.arenaRadius(); step++) {
            Location point = beamOrigin.clone().add(beamDirection.clone().multiply(step));
            point.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, point, 3, 0.1, 0.2, 0.1, 0.01);
            for (Player player : context.playersInArena()) {
                if (hit.contains(player.getUniqueId())) {
                    continue;
                }
                if (player.getLocation().distance(point) <= 1.2) {
                    player.damage(DAMAGE, context.entity());
                    hit.add(player.getUniqueId());
                }
            }
        }
    }

    @Override
    public void onRecoverStart(BossContext context) {
        AstronomerEncounter.from(context).ifPresent(encounter -> encounter.rings().setRotationBoost(0));
    }

    @Override
    public void onCancel(BossContext context) {
        onRecoverStart(context);
    }
}
