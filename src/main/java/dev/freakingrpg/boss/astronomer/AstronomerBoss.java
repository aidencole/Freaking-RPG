package dev.freakingrpg.boss.astronomer;

import dev.freakingrpg.boss.BossAttack;
import dev.freakingrpg.boss.BossDefinition;
import dev.freakingrpg.boss.BossPatternEngine;
import dev.freakingrpg.boss.BossPhase;
import dev.freakingrpg.boss.astronomer.attacks.CelestialShockwaveAttack;
import dev.freakingrpg.boss.astronomer.attacks.ConstellationLaserAttack;
import dev.freakingrpg.boss.astronomer.attacks.EclipseAttack;
import dev.freakingrpg.boss.astronomer.attacks.GreatswordArcAttack;
import dev.freakingrpg.boss.astronomer.attacks.OrbitalPullAttack;
import dev.freakingrpg.boss.astronomer.attacks.PlanetfallAttack;
import dev.freakingrpg.boss.astronomer.attacks.SkyConstellationAttack;
import dev.freakingrpg.boss.astronomer.attacks.SolarBeamAttack;
import java.util.List;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.EntityType;

/**
 * The Astronomer — celestial guardian. Dev arena radius is 36 blocks (~120 ft in production).
 */
public final class AstronomerBoss {

    public static final String ID = "astronomer";

    private static final BossAttack GREATSWORD = new GreatswordArcAttack();
    private static final BossAttack SHOCKWAVE = new CelestialShockwaveAttack();
    private static final BossAttack LASER = new ConstellationLaserAttack();
    private static final BossAttack SOLAR = new SolarBeamAttack();
    private static final BossAttack ORBITAL = new OrbitalPullAttack();
    private static final BossAttack ECLIPSE = new EclipseAttack();
    private static final BossAttack PLANETFALL = new PlanetfallAttack();
    private static final BossAttack SKY = new SkyConstellationAttack();

    private AstronomerBoss() {
    }

    public static BossDefinition create() {
        return new BossDefinition(
            ID,
            Component.text("The Astronomer", NamedTextColor.DARK_AQUA, TextDecoration.BOLD),
            EntityType.ILLUSIONER,
            2500.0,
            36.0,
            80,
            List.of(
                phaseEquilibrium(),
                phaseGravityRotation(),
                phaseFracturedFloor(),
                phaseCelestialOrrery(),
                phaseUnboundCosmos()
            )
        );
    }

    private static BossPhase phaseEquilibrium() {
        return new BossPhase(
            "equilibrium",
            Component.text("The Astronomer — Equilibrium", NamedTextColor.GRAY),
            0.80,
            BossBar.Color.BLUE,
            List.of(
                weighted(GREATSWORD, 3),
                weighted(SHOCKWAVE, 2),
                weighted(LASER, 2)
            )
        );
    }

    private static BossPhase phaseGravityRotation() {
        return new BossPhase(
            "gravity_rotation",
            Component.text("The Astronomer — Gravity Rotation", NamedTextColor.AQUA),
            0.60,
            BossBar.Color.GREEN,
            List.of(
                weighted(SOLAR, 3),
                weighted(ORBITAL, 2),
                weighted(GREATSWORD, 2),
                weighted(LASER, 1)
            )
        );
    }

    private static BossPhase phaseFracturedFloor() {
        return new BossPhase(
            "fractured_floor",
            Component.text("The Astronomer — Fractured Floor", NamedTextColor.YELLOW),
            0.35,
            BossBar.Color.YELLOW,
            List.of(
                weighted(ECLIPSE, 3),
                weighted(PLANETFALL, 3),
                weighted(SOLAR, 2),
                weighted(ORBITAL, 2)
            )
        );
    }

    private static BossPhase phaseCelestialOrrery() {
        return new BossPhase(
            "celestial_orrery",
            Component.text("The Astronomer — Celestial Orrery", NamedTextColor.LIGHT_PURPLE),
            0.12,
            BossBar.Color.PURPLE,
            List.of(
                weighted(SKY, 4),
                weighted(LASER, 2),
                weighted(ECLIPSE, 2),
                weighted(PLANETFALL, 2)
            )
        );
    }

    private static BossPhase phaseUnboundCosmos() {
        return new BossPhase(
            "unbound_cosmos",
            Component.text("The Astronomer — Unbound Cosmos", NamedTextColor.RED),
            0.0,
            BossBar.Color.RED,
            List.of(
                weighted(SKY, 2),
                weighted(SOLAR, 2),
                weighted(PLANETFALL, 2),
                weighted(ORBITAL, 2),
                weighted(ECLIPSE, 2),
                weighted(GREATSWORD, 1)
            )
        );
    }

    private static BossPatternEngine.WeightedAttack weighted(BossAttack attack, int weight) {
        return new BossPatternEngine.WeightedAttack(attack, weight);
    }
}
