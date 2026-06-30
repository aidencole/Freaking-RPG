package dev.freakingrpg.boss;

import net.kyori.adventure.text.Component;

public interface BossAttack {

    String id();

    Component telegraphTitle();

    int telegraphTicks();

    int executeTicks();

    int recoverTicks();

    void onTelegraphStart(BossContext context);

    void onTelegraphTick(BossContext context, int tick);

    void onExecuteStart(BossContext context);

    void onExecuteTick(BossContext context, int tick);

    void onRecoverStart(BossContext context);

    void onRecoverTick(BossContext context, int tick);

    void onCancel(BossContext context);
}
