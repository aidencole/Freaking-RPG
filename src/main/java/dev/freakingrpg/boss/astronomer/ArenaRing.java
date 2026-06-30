package dev.freakingrpg.boss.astronomer;

public final class ArenaRing {

    private final String id;
    private final double innerRadius;
    private final double outerRadius;
    private double angleRadians;
    private double angularVelocity;
    private boolean locked;
    private double tiltRadians;

    public ArenaRing(String id, double innerRadius, double outerRadius, double angularVelocity) {
        this.id = id;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.angularVelocity = angularVelocity;
    }

    public String id() {
        return id;
    }

    public double innerRadius() {
        return innerRadius;
    }

    public double outerRadius() {
        return outerRadius;
    }

    public double angleRadians() {
        return angleRadians;
    }

    public double angularVelocity() {
        return angularVelocity;
    }

    public boolean locked() {
        return locked;
    }

    public double tiltRadians() {
        return tiltRadians;
    }

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setTiltRadians(double tiltRadians) {
        this.tiltRadians = tiltRadians;
    }

    public void tick(double boostMultiplier) {
        if (!locked) {
            angleRadians += angularVelocity * boostMultiplier;
        }
    }

    public boolean contains(double distanceFromCenter) {
        return distanceFromCenter >= innerRadius && distanceFromCenter <= outerRadius;
    }
}
