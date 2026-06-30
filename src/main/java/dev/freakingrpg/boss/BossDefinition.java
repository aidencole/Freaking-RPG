package dev.freakingrpg.boss;

import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;

public final class BossDefinition {

    private final String id;
    private final Component displayName;
    private final EntityType baseEntity;
    private final double maxHealth;
    private final double arenaRadius;
    private final int introTicks;
    private final List<BossPhase> phases;

    public BossDefinition(
        String id,
        Component displayName,
        EntityType baseEntity,
        double maxHealth,
        double arenaRadius,
        int introTicks,
        List<BossPhase> phases
    ) {
        this.id = id;
        this.displayName = displayName;
        this.baseEntity = baseEntity;
        this.maxHealth = maxHealth;
        this.arenaRadius = arenaRadius;
        this.introTicks = introTicks;
        this.phases = List.copyOf(phases);
    }

    public String id() {
        return id;
    }

    public Component displayName() {
        return displayName;
    }

    public EntityType baseEntity() {
        return baseEntity;
    }

    public double maxHealth() {
        return maxHealth;
    }

    public double arenaRadius() {
        return arenaRadius;
    }

    public int introTicks() {
        return introTicks;
    }

    public List<BossPhase> phases() {
        return phases;
    }
}
