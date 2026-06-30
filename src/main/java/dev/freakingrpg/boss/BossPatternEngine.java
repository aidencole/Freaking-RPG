package dev.freakingrpg.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BossPatternEngine {

    private final List<WeightedAttack> attacks;
    private final Random random;
    private String lastAttackId;

    public BossPatternEngine(List<WeightedAttack> attacks, Random random) {
        this.attacks = List.copyOf(attacks);
        this.random = random;
    }

    public BossAttack nextAttack() {
        if (attacks.isEmpty()) {
            throw new IllegalStateException("Boss phase has no attacks configured.");
        }

        List<WeightedAttack> pool = new ArrayList<>();
        int totalWeight = 0;
        for (WeightedAttack entry : attacks) {
            if (entry.attack().id().equals(lastAttackId) && attacks.size() > 1) {
                continue;
            }
            pool.add(entry);
            totalWeight += entry.weight();
        }

        if (pool.isEmpty()) {
            pool.addAll(attacks);
            totalWeight = attacks.stream().mapToInt(WeightedAttack::weight).sum();
        }

        int roll = random.nextInt(totalWeight);
        int cursor = 0;
        for (WeightedAttack entry : pool) {
            cursor += entry.weight();
            if (roll < cursor) {
                lastAttackId = entry.attack().id();
                return entry.attack();
            }
        }

        lastAttackId = pool.getLast().attack().id();
        return pool.getLast().attack();
    }

    public record WeightedAttack(BossAttack attack, int weight) {
    }
}
