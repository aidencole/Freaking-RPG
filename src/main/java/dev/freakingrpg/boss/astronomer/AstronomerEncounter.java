package dev.freakingrpg.boss.astronomer;

import dev.freakingrpg.FreakingRpgPlugin;
import dev.freakingrpg.boss.BossContext;
import dev.freakingrpg.boss.encounter.BossEncounter;
import java.util.Random;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;

public final class AstronomerEncounter implements BossEncounter {

    private final FreakingRpgPlugin plugin;
    private final ObservatoryArena arena;
    private final RingRotationEngine rings;
    private final GravityField gravity;
    private int tick;
    private int gravityTimer;
    private int phaseIndex;

    public AstronomerEncounter(
        FreakingRpgPlugin plugin,
        ObservatoryArena arena,
        RingRotationEngine rings,
        GravityField gravity
    ) {
        this.plugin = plugin;
        this.arena = arena;
        this.rings = rings;
        this.gravity = gravity;
        this.gravityTimer = 600;
    }

    public static Optional<AstronomerEncounter> from(BossContext context) {
        if (context.instance().encounter() instanceof AstronomerEncounter encounter) {
            return Optional.of(encounter);
        }
        return Optional.empty();
    }

    public ObservatoryArena arena() {
        return arena;
    }

    public RingRotationEngine rings() {
        return rings;
    }

    public GravityField gravity() {
        return gravity;
    }

    @Override
    public void onStart(BossContext context) {
        rings.setEnabled(true);
        context.playersInArena().forEach(player ->
            player.sendMessage(Component.text(
                "The Astronomer regards you with ancient disappointment.",
                NamedTextColor.GRAY
            ))
        );
        context.entity().getWorld().playSound(arena.center(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 0.5f);
        onPhaseEnter(context, 0);
    }

    @Override
    public void onTick(BossContext context) {
        tick++;
        arena.tickScenery(tick);
        rings.tick(context.playersInArena());
        gravity.tick(context.playersInArena());

        if (phaseIndex >= 1 && !gravity.isTelegraphing()) {
            gravityTimer--;
            if (gravityTimer <= 0) {
                triggerGravityShift();
            }
        }
    }

    @Override
    public void onPhaseEnter(BossContext context, int phaseIndex) {
        this.phaseIndex = phaseIndex;
        switch (phaseIndex) {
            case 0 -> applyEquilibrium();
            case 1 -> applyGravityRotationPhase(context);
            case 2 -> applyFracturedFloor();
            case 3 -> applyCelestialOrrery(context);
            case 4 -> applyUnboundCosmos();
            default -> {
            }
        }
    }

    @Override
    public void onStop(BossContext context) {
        rings.shutdown();
        arena.shutdown();
        plugin.astronomerArenaService().clearPlatform();
        gravity.setImmediate(GravityAlignment.NORMAL);
    }

    private void applyEquilibrium() {
        rings.setRotationBoost(0);
        gravity.setImmediate(GravityAlignment.NORMAL);
        gravityTimer = 600;
    }

    private void applyGravityRotationPhase(BossContext context) {
        context.instance().bossBar().setTitle(Component.text("Gravity Rotation", NamedTextColor.AQUA));
        rings.setRotationBoost(0.4);
        gravityTimer = 600;
        triggerGravityShift();
    }

    private void applyFracturedFloor() {
        rings.setRotationBoost(0.8);
        rings.rings().get(0).setAngularVelocity(0.009);
        rings.rings().get(1).setAngularVelocity(-0.013);
        rings.rings().get(2).setAngularVelocity(0.016);
        rings.rings().get(1).setTiltRadians(0.25);
        rings.rings().get(0).setLocked(false);
        rings.rings().get(2).setLocked(false);
        gravityTimer = 500;
    }

    private void applyCelestialOrrery(BossContext context) {
        context.entity().getWorld().playSound(arena.center(), Sound.BLOCK_END_PORTAL_SPAWN, 0.8f, 0.7f);
        rings.setRotationBoost(1.0);
        gravityTimer = 450;
    }

    private void applyUnboundCosmos() {
        rings.setRotationBoost(1.4);
        gravityTimer = 300;
        triggerGravityShift();
    }

    private void triggerGravityShift() {
        GravityAlignment next = phaseIndex >= 4
            ? GravityAlignment.randomUnstable(new Random())
            : switch (phaseIndex) {
                case 1 -> GravityAlignment.EAST;
                case 2 -> GravityAlignment.INVERTED;
                case 3 -> GravityAlignment.SOUTH;
                default -> GravityAlignment.NORMAL;
            };
        gravity.scheduleShift(next, 100);
        gravityTimer = phaseIndex >= 4 ? 300 : 600;
        arena.center().getWorld().playSound(arena.center(), Sound.BLOCK_GRINDSTONE_USE, 1.0f, 0.5f);
    }
}
