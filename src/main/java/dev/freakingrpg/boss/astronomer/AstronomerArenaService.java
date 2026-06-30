package dev.freakingrpg.boss.astronomer;

import dev.freakingrpg.FreakingRpgPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

public final class AstronomerArenaService {

    public static final String WORLD_NAME = "frpg_observatory";
    private static final double ARENA_RADIUS = 36.0;
    private static final int FLOOR_Y = 80;

    private final FreakingRpgPlugin plugin;
    private final List<Block> activePlatform = new ArrayList<>();
    private World arenaWorld;

    public AstronomerArenaService(FreakingRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public Location prepareArena(Player player) {
        World world = getOrCreateWorld();
        Location floorCenter = new Location(world, 0, FLOOR_Y, 0);

        ObservatoryPlatformBuilder.clear(activePlatform);
        activePlatform.addAll(ObservatoryPlatformBuilder.build(floorCenter, ARENA_RADIUS));
        plugin.getLogger().info("Observatory platform placed " + activePlatform.size() + " blocks at Y=" + FLOOR_Y);

        Location arenaCenter = floorCenter.clone().add(0.5, 1, 0.5);
        Location playerSpawn = arenaCenter.clone().add(0, 0, ARENA_RADIUS - 4);
        player.teleport(playerSpawn);
        player.sendMessage(plugin.brandedMessage(
            "You enter the Observatory. Stand on the quartz rings — the arena is the boss."
        ));

        return arenaCenter;
    }

    public void clearPlatform() {
        ObservatoryPlatformBuilder.clear(activePlatform);
    }

    public double arenaRadius() {
        return ARENA_RADIUS;
    }

    public World arenaWorld() {
        return getOrCreateWorld();
    }

    private World getOrCreateWorld() {
        if (arenaWorld != null) {
            return arenaWorld;
        }

        World existing = Bukkit.getWorld(WORLD_NAME);
        if (existing != null) {
            arenaWorld = existing;
            configureWorld(arenaWorld);
            return arenaWorld;
        }

        WorldCreator creator = new WorldCreator(WORLD_NAME);
        creator.generator(new VoidChunkGenerator());
        creator.environment(World.Environment.NORMAL);
        arenaWorld = creator.createWorld();
        if (arenaWorld == null) {
            throw new IllegalStateException("Failed to create observatory world '" + WORLD_NAME + "'.");
        }
        configureWorld(arenaWorld);
        plugin.getLogger().info("Created observatory arena world: " + WORLD_NAME);
        return arenaWorld;
    }

    private void configureWorld(World world) {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setTime(18000);
        world.setSpawnLocation(new Location(world, 0.5, FLOOR_Y + 1, ARENA_RADIUS - 4));
    }

    private static final class VoidChunkGenerator extends ChunkGenerator {
        @Override
        public boolean shouldGenerateNoise() {
            return false;
        }

        @Override
        public boolean shouldGenerateSurface() {
            return false;
        }

        @Override
        public boolean shouldGenerateCaves() {
            return false;
        }

        @Override
        public boolean shouldGenerateDecorations() {
            return false;
        }

        @Override
        public boolean shouldGenerateMobs() {
            return false;
        }

        @Override
        public boolean shouldGenerateStructures() {
            return false;
        }
    }
}
