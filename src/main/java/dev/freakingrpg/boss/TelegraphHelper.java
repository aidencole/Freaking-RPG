package dev.freakingrpg.boss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class TelegraphHelper {

    private TelegraphHelper() {
    }

    public static void ring(Location center, double radius, Particle particle, int points) {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        double y = center.getY() + 0.1;
        for (int i = 0; i < points; i++) {
            double angle = (Math.PI * 2 * i) / points;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            world.spawnParticle(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    public static void dangerRing(Location center, double radius, int points) {
        ring(center, radius, Particle.DUST, points, Color.RED, 1.5f);
    }

    public static void warningRing(Location center, double radius, int points) {
        ring(center, radius, Particle.DUST, points, Color.ORANGE, 1.2f);
    }

    public static void line(Location start, Vector direction, double length, Particle particle, int points) {
        World world = start.getWorld();
        if (world == null) {
            return;
        }

        Vector step = direction.clone().normalize().multiply(length / points);
        Location cursor = start.clone().add(0, 0.1, 0);
        for (int i = 0; i < points; i++) {
            world.spawnParticle(particle, cursor, 1, 0, 0, 0, 0);
            cursor.add(step);
        }
    }

    public static void spiral(Location center, double radius, int arms, int pointsPerArm, int tick) {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        for (int arm = 0; arm < arms; arm++) {
            for (int point = 0; point < pointsPerArm; point++) {
                double progress = (point + (tick * 0.15)) / pointsPerArm;
                double angle = (Math.PI * 2 * arm / arms) + progress * Math.PI * 2;
                double currentRadius = radius * progress;
                double x = center.getX() + Math.cos(angle) * currentRadius;
                double z = center.getZ() + Math.sin(angle) * currentRadius;
                world.spawnParticle(Particle.END_ROD, x, center.getY() + 1.0, z, 1, 0, 0, 0, 0);
            }
        }
    }

    public static void playTelegraphSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1.0f, 0.8f);
    }

    public static void playTelegraphSound(Collection<Player> players, Sound sound) {
        for (Player player : players) {
            playTelegraphSound(player, sound);
        }
    }

    private static void ring(Location center, double radius, Particle particle, int points, Color color, float size) {
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        Particle.DustOptions dust = new Particle.DustOptions(color, size);
        double y = center.getY() + 0.1;
        for (int i = 0; i < points; i++) {
            double angle = (Math.PI * 2 * i) / points;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            world.spawnParticle(particle, x, y, z, 1, 0, 0, 0, 0, dust);
        }
    }

    public static List<Location> circlePoints(Location center, double radius, int points) {
        List<Location> locations = new ArrayList<>(points);
        for (int i = 0; i < points; i++) {
            double angle = (Math.PI * 2 * i) / points;
            locations.add(center.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius));
        }
        return locations;
    }
}
