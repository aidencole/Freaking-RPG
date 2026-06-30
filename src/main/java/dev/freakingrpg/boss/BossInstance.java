package dev.freakingrpg.boss;

import dev.freakingrpg.FreakingRpgPlugin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.Random;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public final class BossInstance {

    private final UUID id = UUID.randomUUID();
    private final FreakingRpgPlugin plugin;
    private final BossDefinition definition;
    private final LivingEntity entity;
    private final BossContext context;
    private final BossBarController bossBar;
    private final Random random;

    private int phaseIndex;
    private BossPatternEngine patternEngine;
    private BossFightState state = BossFightState.INTRO;
    private BossAttack currentAttack;
    private int stateTicksRemaining;
    private ScheduledTask tickTask;

    public BossInstance(
        FreakingRpgPlugin plugin,
        BossDefinition definition,
        LivingEntity entity,
        Location arenaCenter,
        double arenaRadius,
        Random random
    ) {
        this.plugin = plugin;
        this.definition = definition;
        this.entity = entity;
        this.context = new BossContext(this, entity, arenaCenter, arenaRadius);
        this.random = random;
        this.phaseIndex = 0;
        this.patternEngine = createPatternEngine(phaseIndex);
        this.bossBar = new BossBarController(
            definition.displayName(),
            definition.phases().getFirst().barColor()
        );
        this.stateTicksRemaining = definition.introTicks();
    }

    public UUID id() {
        return id;
    }

    public BossDefinition definition() {
        return definition;
    }

    public LivingEntity entity() {
        return entity;
    }

    public BossContext context() {
        return context;
    }

    public BossFightState state() {
        return state;
    }

    public BossBarController bossBar() {
        return bossBar;
    }

    public boolean isVulnerable() {
        return state == BossFightState.RECOVER;
    }

    public void start() {
        bossBar.show(context.playersInArena());
        tickTask = entity.getScheduler().runAtFixedRate(plugin, task -> tick(), null, 1L, 1L);
    }

    public void stop() {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
        if (currentAttack != null) {
            currentAttack.onCancel(context);
            currentAttack = null;
        }
        bossBar.hide(context.playersInArena());
    }

    private void tick() {
        if (!entity.isValid() || entity.isDead()) {
            stop();
            return;
        }

        syncBossBar();
        refreshAudience();

        switch (state) {
            case INTRO -> tickIntro();
            case TELEGRAPH -> tickTelegraph();
            case EXECUTE -> tickExecute();
            case RECOVER -> tickRecover();
            case PHASE_SHIFT -> tickPhaseShift();
            case DEFEATED -> stop();
        }
    }

    private void tickIntro() {
        if (stateTicksRemaining == definition.introTicks()) {
            entity.getWorld().playSound(entity.getLocation(), org.bukkit.Sound.EVENT_RAID_HORN, 1.2f, 0.7f);
        }

        if (--stateTicksRemaining <= 0) {
            beginTelegraph(selectNextAttack());
        }
    }

    private void tickTelegraph() {
        int elapsed = currentAttack.telegraphTicks() - stateTicksRemaining;
        currentAttack.onTelegraphTick(context, elapsed);
        if (--stateTicksRemaining <= 0) {
            beginExecute();
        }
    }

    private void tickExecute() {
        int elapsed = currentAttack.executeTicks() - stateTicksRemaining;
        currentAttack.onExecuteTick(context, elapsed);
        if (--stateTicksRemaining <= 0) {
            beginRecover();
        }
    }

    private void tickRecover() {
        int elapsed = currentAttack.recoverTicks() - stateTicksRemaining;
        currentAttack.onRecoverTick(context, elapsed);
        if (--stateTicksRemaining <= 0) {
            if (shouldShiftPhase()) {
                beginPhaseShift();
            } else {
                beginTelegraph(selectNextAttack());
            }
        }
    }

    private void tickPhaseShift() {
        if (--stateTicksRemaining <= 0) {
            phaseIndex = Math.min(phaseIndex + 1, definition.phases().size() - 1);
            patternEngine = createPatternEngine(phaseIndex);
            BossPhase phase = definition.phases().get(phaseIndex);
            bossBar.setTitle(phase.title());
            bossBar.setColor(phase.barColor());
            entity.getWorld().playSound(entity.getLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.8f);
            beginTelegraph(selectNextAttack());
        }
    }

    private void beginTelegraph(BossAttack attack) {
        if (currentAttack != null) {
            currentAttack.onCancel(context);
        }
        currentAttack = attack;
        state = BossFightState.TELEGRAPH;
        stateTicksRemaining = attack.telegraphTicks();
        bossBar.setTitle(attack.telegraphTitle());
        attack.onTelegraphStart(context);
    }

    private void beginExecute() {
        state = BossFightState.EXECUTE;
        stateTicksRemaining = currentAttack.executeTicks();
        bossBar.setTitle(definition.phases().get(phaseIndex).title());
        currentAttack.onExecuteStart(context);
    }

    private void beginRecover() {
        state = BossFightState.RECOVER;
        stateTicksRemaining = currentAttack.recoverTicks();
        currentAttack.onRecoverStart(context);
    }

    private void beginPhaseShift() {
        state = BossFightState.PHASE_SHIFT;
        stateTicksRemaining = 50;
        bossBar.setTitle(Component.text("Phase Shift!", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
        entity.getWorld().spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING, entity.getLocation().add(0, 1, 0), 40, 0.8, 1.0, 0.8, 0.05);
    }

    public void onDefeated() {
        state = BossFightState.DEFEATED;
        stop();
    }

    private BossAttack selectNextAttack() {
        return patternEngine.nextAttack();
    }

    private boolean shouldShiftPhase() {
        if (phaseIndex >= definition.phases().size() - 1) {
            return false;
        }

        double healthRatio = entity.getHealth() / entity.getAttribute(Attribute.MAX_HEALTH).getValue();
        return healthRatio <= definition.phases().get(phaseIndex).healthThreshold();
    }

    private BossPatternEngine createPatternEngine(int index) {
        return new BossPatternEngine(definition.phases().get(index).attacks(), random);
    }

    private void syncBossBar() {
        double max = entity.getAttribute(Attribute.MAX_HEALTH).getValue();
        bossBar.setProgress((float) (entity.getHealth() / max));
    }

    private void refreshAudience() {
        bossBar.show(context.playersInArena());
    }
}
