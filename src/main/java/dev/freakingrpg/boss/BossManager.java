package dev.freakingrpg.boss;

import dev.freakingrpg.FreakingRpgPlugin;
import dev.freakingrpg.boss.astronomer.AstronomerBoss;
import dev.freakingrpg.boss.astronomer.AstronomerEncounter;
import dev.freakingrpg.boss.astronomer.GravityField;
import dev.freakingrpg.boss.astronomer.ObservatoryArena;
import dev.freakingrpg.boss.astronomer.RingRotationEngine;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public final class BossManager {

    private static final double MAX_ENTITY_HEALTH = 1024.0;

    private final FreakingRpgPlugin plugin;
    private final BossRegistry registry;
    private final BossKeys keys;
    private final Random random = new Random();
    private final Map<UUID, BossInstance> instancesById = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> instanceIdByEntity = new ConcurrentHashMap<>();

    public BossManager(FreakingRpgPlugin plugin, BossRegistry registry, BossKeys keys) {
        this.plugin = plugin;
        this.registry = registry;
        this.keys = keys;
    }

    public BossRegistry registry() {
        return registry;
    }

    public BossKeys keys() {
        return keys;
    }

    public Optional<BossInstance> spawn(String bossId, Location location) {
        return spawn(bossId, location, null);
    }

    public Optional<BossInstance> spawn(String bossId, Location location, Player spawner) {
        Optional<BossDefinition> definition = registry.find(bossId);
        if (definition.isEmpty()) {
            return Optional.empty();
        }

        try {
            Location spawnLocation = location;
            if (AstronomerBoss.ID.equals(bossId) && spawner != null) {
                spawnLocation = plugin.astronomerArenaService().prepareArena(spawner);
            }
            return Optional.of(spawn(definition.get(), spawnLocation));
        } catch (Exception exception) {
            plugin.getLogger().severe("Failed to spawn boss " + bossId + ": " + exception.getMessage());
            exception.printStackTrace();
            if (spawner != null) {
                spawner.sendMessage(plugin.brandedMessage("Boss spawn failed: " + exception.getMessage()));
            }
            return Optional.empty();
        }
    }

    public BossInstance spawn(BossDefinition definition, Location location) {
        double arenaRadius = definition.arenaRadius();
        if (AstronomerBoss.ID.equals(definition.id())) {
            arenaRadius = plugin.astronomerArenaService().arenaRadius();
        }

        Location entitySpawn = location.clone().add(0, 1, 0);
        LivingEntity entity = (LivingEntity) entitySpawn.getWorld().spawnEntity(entitySpawn, definition.baseEntity());
        configureEntity(entity, definition);

        BossInstance instance = new BossInstance(
            plugin,
            definition,
            entity,
            location,
            arenaRadius,
            random
        );

        entity.getPersistentDataContainer().set(keys.instanceId(), PersistentDataType.STRING, instance.id().toString());
        instancesById.put(instance.id(), instance);
        instanceIdByEntity.put(entity.getUniqueId(), instance.id());

        if (AstronomerBoss.ID.equals(definition.id())) {
            attachAstronomerEncounter(instance, location, arenaRadius);
        }

        instance.start();
        return instance;
    }

    private void attachAstronomerEncounter(BossInstance instance, Location center, double arenaRadius) {
        ObservatoryArena arena = ObservatoryArena.build(plugin, center, arenaRadius);
        RingRotationEngine rings = new RingRotationEngine(plugin, center, arenaRadius);
        rings.buildVisuals();
        GravityField gravity = new GravityField(plugin);
        instance.attachEncounter(new AstronomerEncounter(plugin, arena, rings, gravity));
    }

    private void configureEntity(LivingEntity entity, BossDefinition definition) {
        double maxHealth = Math.min(MAX_ENTITY_HEALTH, definition.maxHealth());

        entity.customName(definition.displayName());
        entity.setCustomNameVisible(true);
        entity.setAI(false);
        entity.setPersistent(true);
        entity.setRemoveWhenFarAway(false);
        entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
        entity.setHealth(maxHealth);
        entity.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
        entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.0);
        if (AstronomerBoss.ID.equals(definition.id()) && entity.getAttribute(Attribute.SCALE) != null) {
            entity.getAttribute(Attribute.SCALE).setBaseValue(2.2);
        }
    }

    public Optional<BossInstance> findByEntity(UUID entityId) {
        UUID instanceId = instanceIdByEntity.get(entityId);
        if (instanceId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(instancesById.get(instanceId));
    }

    public Optional<BossInstance> find(UUID instanceId) {
        return Optional.ofNullable(instancesById.get(instanceId));
    }

    public Collection<BossInstance> activeInstances() {
        return instancesById.values();
    }

    public void stop(BossInstance instance) {
        instance.stop();
        instancesById.remove(instance.id());
        instanceIdByEntity.remove(instance.entity().getUniqueId());
        if (instance.entity().isValid()) {
            instance.entity().remove();
        }
    }

    public void stopAll() {
        for (BossInstance instance : instancesById.values().toArray(BossInstance[]::new)) {
            stop(instance);
        }
    }

    public void onBossDefeated(BossInstance instance) {
        instance.onDefeated();
        instancesById.remove(instance.id());
        instanceIdByEntity.remove(instance.entity().getUniqueId());
    }

    public void shutdown() {
        stopAll();
    }
}
