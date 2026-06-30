package dev.freakingrpg.boss.astronomer;

import org.bukkit.util.Vector;

public enum GravityAlignment {
    NORMAL(new Vector(0, -0.12, 0), "Gravity stabilizes."),
    EAST(new Vector(0.12, 0, 0), "Gravity pulls eastward."),
    WEST(new Vector(-0.12, 0, 0), "Gravity pulls westward."),
    SOUTH(new Vector(0, 0, 0.12), "Gravity pulls southward."),
    NORTH(new Vector(0, 0, -0.12), "Gravity pulls northward."),
    INVERTED(new Vector(0, 0.12, 0), "Gravity inverts.");

    private final Vector acceleration;
    private final String warning;

    GravityAlignment(Vector acceleration, String warning) {
        this.acceleration = acceleration;
        this.warning = warning;
    }

    public Vector acceleration() {
        return acceleration.clone();
    }

    public String warning() {
        return warning;
    }

    public static GravityAlignment randomUnstable(java.util.Random random) {
        GravityAlignment[] values = values();
        return values[random.nextInt(values.length)];
    }
}
