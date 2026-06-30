package dev.freakingrpg.boss;

import java.util.List;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class BossPhase {

    private final String id;
    private final Component title;
    private final double healthThreshold;
    private final BossBar.Color barColor;
    private final List<BossPatternEngine.WeightedAttack> attacks;

    public BossPhase(
        String id,
        Component title,
        double healthThreshold,
        BossBar.Color barColor,
        List<BossPatternEngine.WeightedAttack> attacks
    ) {
        this.id = id;
        this.title = title;
        this.healthThreshold = healthThreshold;
        this.barColor = barColor;
        this.attacks = List.copyOf(attacks);
    }

    public String id() {
        return id;
    }

    public Component title() {
        return title;
    }

    public double healthThreshold() {
        return healthThreshold;
    }

    public BossBar.Color barColor() {
        return barColor;
    }

    public List<BossPatternEngine.WeightedAttack> attacks() {
        return attacks;
    }
}
