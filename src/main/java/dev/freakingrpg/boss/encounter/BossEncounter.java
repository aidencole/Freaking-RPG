package dev.freakingrpg.boss.encounter;

import dev.freakingrpg.boss.BossContext;

public interface BossEncounter {

    void onStart(BossContext context);

    void onTick(BossContext context);

    void onPhaseEnter(BossContext context, int phaseIndex);

    void onStop(BossContext context);
}
