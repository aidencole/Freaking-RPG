package dev.freakingrpg.presentation;

import org.bukkit.entity.Entity;

/**
 * Plays authored animations on a boss body. Vanilla ravagers cannot do real attack
 * animations; production bosses should be backed by ModelEngine (or similar) with
 * Blockbench-authored bones.
 */
public interface BossAnimator {

    void play(Entity entity, String animationId, AnimationMode mode);

    void stop(Entity entity, String animationId);

    boolean isAvailable();

    enum AnimationMode {
        /** Stop other animations before playing, e.g. a heavy slam. */
        SINGLE,
        /** Layer additive motion, e.g. breathing + arm flare. */
        MULTIPLE
    }

    static BossAnimator noop() {
        return new BossAnimator() {
            @Override
            public void play(Entity entity, String animationId, AnimationMode mode) {
            }

            @Override
            public void stop(Entity entity, String animationId) {
            }

            @Override
            public boolean isAvailable() {
                return false;
            }
        };
    }
}
