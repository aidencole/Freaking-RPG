package dev.freakingrpg.boss.builtin;

import dev.freakingrpg.boss.BossAttack;
import dev.freakingrpg.boss.BossDefinition;
import dev.freakingrpg.boss.BossPatternEngine;
import dev.freakingrpg.boss.BossPhase;
import dev.freakingrpg.boss.attacks.ChargeRushAttack;
import dev.freakingrpg.boss.attacks.LeapSlamAttack;
import dev.freakingrpg.boss.attacks.RadialBurstAttack;
import java.util.List;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.EntityType;

public final class ColossusDrillBoss {

    public static final String ID = "colossus_drill";

    private static final BossAttack LEAP_SLAM = new LeapSlamAttack();
    private static final BossAttack CHARGE_RUSH = new ChargeRushAttack();
    private static final BossAttack RADIAL_BURST = new RadialBurstAttack();

    private ColossusDrillBoss() {
    }

    public static BossDefinition create() {
        BossPhase phaseOne = new BossPhase(
            "awakening",
            Component.text("Colossus Drill", NamedTextColor.GOLD, TextDecoration.BOLD),
            0.60,
            BossBar.Color.YELLOW,
            List.of(
                new BossPatternEngine.WeightedAttack(LEAP_SLAM, 3),
                new BossPatternEngine.WeightedAttack(CHARGE_RUSH, 2),
                new BossPatternEngine.WeightedAttack(RADIAL_BURST, 2)
            )
        );

        BossPhase phaseTwo = new BossPhase(
            "enraged",
            Component.text("Colossus Drill", NamedTextColor.RED, TextDecoration.BOLD),
            0.0,
            BossBar.Color.RED,
            List.of(
                new BossPatternEngine.WeightedAttack(CHARGE_RUSH, 3),
                new BossPatternEngine.WeightedAttack(RADIAL_BURST, 3),
                new BossPatternEngine.WeightedAttack(LEAP_SLAM, 2)
            )
        );

        return new BossDefinition(
            ID,
            Component.text("Colossus Drill", NamedTextColor.GOLD, TextDecoration.BOLD),
            EntityType.RAVAGER,
            500.0,
            24.0,
            60,
            List.of(phaseOne, phaseTwo)
        );
    }
}
