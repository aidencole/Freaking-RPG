package dev.freakingrpg.boss.astronomer.attacks;

import dev.freakingrpg.boss.AbstractBossAttack;
import dev.freakingrpg.boss.BossContext;
import dev.freakingrpg.boss.TelegraphHelper;
import dev.freakingrpg.vfx.effects.ImpactCraterEffect;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class PlanetfallAttack extends AbstractBossAttack {

    private static final double DAMAGE = 15.0;
    private final List<Location> impactSites = new ArrayList<>();
    private final Set<UUID> hit = new HashSet<>();

    public PlanetfallAttack() {
        super("planetfall", Component.text("Planetfall", NamedTextColor.RED), 55, 30, 35);
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        hit.clear();
        impactSites.clear();
        Location center = context.arenaCenter();
        for (int i = 0; i < 4; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = 6 + Math.random() * (context.arenaRadius() - 8);
            impactSites.add(center.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius));
        }
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.ENTITY_WITHER_SPAWN);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        for (Location site : impactSites) {
            Location sky = site.clone().add(0, 18, 0);
            site.getWorld().spawnParticle(Particle.FALLING_DUST, sky, 8, 0.4, 0.2, 0.4, 0, site.getBlock().getBlockData());
            TelegraphHelper.warningRing(site, 3.0, 14);
        }
    }

    @Override
    public void onExecuteStart(BossContext context) {
        for (Location site : impactSites) {
            site.getWorld().playSound(site, Sound.ENTITY_GENERIC_EXPLODE, 0.9f, 0.5f);
            ImpactCraterEffect.burst(context.plugin(), site, 4.5, 14);
        }
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        for (Location site : impactSites) {
            for (Player player : context.playersInArena()) {
                if (hit.contains(player.getUniqueId())) {
                    continue;
                }
                if (player.getLocation().distance(site) <= 4.0) {
                    player.damage(DAMAGE, context.entity());
                    hit.add(player.getUniqueId());
                }
            }
        }
    }
}
