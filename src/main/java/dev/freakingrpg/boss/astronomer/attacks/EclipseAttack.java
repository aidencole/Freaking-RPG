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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class EclipseAttack extends AbstractBossAttack {

    private static final double DAMAGE = 6.0;
    private final Set<Integer> safeSectors = new HashSet<>();
    private final Set<UUID> hit = new HashSet<>();

    public EclipseAttack() {
        super("eclipse", Component.text("Eclipse", NamedTextColor.DARK_PURPLE), 60, 50, 30);
    }

    @Override
    public void onTelegraphStart(BossContext context) {
        hit.clear();
        safeSectors.clear();
        for (int i = 0; i < 3; i++) {
            safeSectors.add((int) (Math.random() * 6));
        }
        for (Player player : context.playersInArena()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0, false, false, false));
        }
        TelegraphHelper.playTelegraphSound(context.playersInArena(), Sound.AMBIENT_CAVE);
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
        markSafeStars(context);
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
        markSafeStars(context);
        punishUnsafeSectors(context);
    }

    private void markSafeStars(BossContext context) {
        Location center = context.arenaCenter();
        for (int sector = 0; sector < 6; sector++) {
            if (!safeSectors.contains(sector)) {
                continue;
            }
            double angle = (Math.PI * 2 * sector) / 6;
            double radius = context.arenaRadius() * 0.55;
            center.getWorld().spawnParticle(
                Particle.GLOW,
                center.getX() + Math.cos(angle) * radius,
                center.getY() + 1,
                center.getZ() + Math.sin(angle) * radius,
                4,
                0.2,
                0.2,
                0.2,
                0.01
            );
        }
    }

    private void punishUnsafeSectors(BossContext context) {
        Location center = context.arenaCenter();
        for (Player player : context.playersInArena()) {
            if (hit.contains(player.getUniqueId())) {
                continue;
            }
            int sector = sectorFor(player.getLocation(), center);
            if (safeSectors.contains(sector)) {
                continue;
            }
            player.damage(DAMAGE, context.entity());
            hit.add(player.getUniqueId());
        }
    }

    private static int sectorFor(Location player, Location center) {
        double angle = Math.atan2(player.getZ() - center.getZ(), player.getX() - center.getX());
        int sector = (int) Math.floor((angle + Math.PI) / (Math.PI / 3));
        return Math.floorMod(sector, 6);
    }
}
