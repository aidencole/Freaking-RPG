package dev.freakingrpg.boss;

import net.kyori.adventure.text.Component;

public abstract class AbstractBossAttack implements BossAttack {

    private final String id;
    private final Component telegraphTitle;
    private final int telegraphTicks;
    private final int executeTicks;
    private final int recoverTicks;

    protected AbstractBossAttack(
        String id,
        Component telegraphTitle,
        int telegraphTicks,
        int executeTicks,
        int recoverTicks
    ) {
        this.id = id;
        this.telegraphTitle = telegraphTitle;
        this.telegraphTicks = telegraphTicks;
        this.executeTicks = executeTicks;
        this.recoverTicks = recoverTicks;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Component telegraphTitle() {
        return telegraphTitle;
    }

    @Override
    public int telegraphTicks() {
        return telegraphTicks;
    }

    @Override
    public int executeTicks() {
        return executeTicks;
    }

    @Override
    public int recoverTicks() {
        return recoverTicks;
    }

    @Override
    public void onTelegraphStart(BossContext context) {
    }

    @Override
    public void onTelegraphTick(BossContext context, int tick) {
    }

    @Override
    public void onExecuteStart(BossContext context) {
    }

    @Override
    public void onExecuteTick(BossContext context, int tick) {
    }

    @Override
    public void onRecoverStart(BossContext context) {
    }

    @Override
    public void onRecoverTick(BossContext context, int tick) {
    }

    @Override
    public void onCancel(BossContext context) {
    }
}
