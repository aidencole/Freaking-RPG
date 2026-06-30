package dev.freakingrpg.boss;

import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class BossContext {

    private final BossInstance instance;
    private final LivingEntity entity;
    private final Location arenaCenter;
    private final double arenaRadius;

    public BossContext(BossInstance instance, LivingEntity entity, Location arenaCenter, double arenaRadius) {
        this.instance = instance;
        this.entity = entity;
        this.arenaCenter = arenaCenter.clone();
        this.arenaRadius = arenaRadius;
    }

    public BossInstance instance() {
        return instance;
    }

    public LivingEntity entity() {
        return entity;
    }

    public Location arenaCenter() {
        return arenaCenter.clone();
    }

    public double arenaRadius() {
        return arenaRadius;
    }

    public List<Player> playersInArena() {
        return arenaCenter.getWorld().getPlayers().stream()
            .filter(player -> player.getLocation().distanceSquared(arenaCenter) <= arenaRadius * arenaRadius)
            .filter(player -> !player.isDead())
            .toList();
    }

    public Player nearestPlayer() {
        Player nearest = null;
        double best = Double.MAX_VALUE;
        for (Player player : playersInArena()) {
            double distance = player.getLocation().distanceSquared(entity.getLocation());
            if (distance < best) {
                best = distance;
                nearest = player;
            }
        }
        return nearest;
    }

    public UUID instanceId() {
        return instance.id();
    }
}
