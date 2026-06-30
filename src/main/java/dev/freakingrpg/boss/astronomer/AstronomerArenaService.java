package dev.freakingrpg.boss.astronomer;

import dev.freakingrpg.FreakingRpgPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class AstronomerArenaService {

    public static final String WORLD_NAME = "frpg_observatory";
    public static final int FLOOR_Y = 80;

    private static final double ARENA_RADIUS = 36.0;

    private final FreakingRpgPlugin plugin;
    private final List<Block> activePlatform = new ArrayList<>();
    private World arenaWorld;

    public AstronomerArenaService(FreakingRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public Location prepareArena(Player player) {
        World world = getOrCreateWorld();
        Location floorCenter = new Location(world, 0, FLOOR_Y, 0);
        Location arenaCenter = floorCenter.clone().add(0.5, 1, 0.5);
        Location playerSpawn = arenaCenter.clone().add(0, 0, ARENA_RADIUS - 4);

        ArenaChunkLoader.ensureLoaded(world, floorCenter, ARENA_RADIUS + 4);

        ObservatoryPlatformBuilder.clear(activePlatform);
        activePlatform.addAll(ObservatoryPlatformBuilder.build(floorCenter, ARENA_RADIUS));
        plugin.getLogger().info(
            "Observatory platform placed " + activePlatform.size() + " blocks at Y=" + FLOOR_Y
                + " in world " + world.getName()
        );

        if (world.getBlockAt(0, FLOOR_Y + 1, 0).getType() == Material.AIR) {
            plugin.getLogger().warning(
                "Observatory floor is still air at (0, " + (FLOOR_Y + 1) + ", 0). "
                    + "Stop the server, delete the world folder 'frpg_observatory', then run again."
            );
        }

        ArenaChunkLoader.refreshArena(world, floorCenter, ARENA_RADIUS + 4);

        world.getChunkAtAsync(playerSpawn).thenAccept(chunk -> Bukkit.getScheduler().runTask(plugin, () -> {
            ArenaChunkLoader.refreshArena(world, floorCenter, ARENA_RADIUS + 4);
            player.teleport(playerSpawn);
            ArenaChunkLoader.syncBlocksToPlayer(player, activePlatform, ARENA_RADIUS + 6);

            plugin.getLogger().info(
                "Player " + player.getName()
                    + " teleported to observatory at "
                    + formatLocation(player.getLocation())
            );

            player.sendMessage(plugin.brandedMessage(
                "You enter the Observatory. The stone rings beneath you are the arena."
            ));

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline()) {
                    return;
                }
                ArenaChunkLoader.refreshArena(world, floorCenter, ARENA_RADIUS + 4);
                ArenaChunkLoader.syncBlocksToPlayer(player, activePlatform, ARENA_RADIUS + 6);
            }, 10L);
        }));

        return arenaCenter;
    }

    public void clearPlatform() {
        World world = arenaWorld;
        if (world != null) {
            Location floorCenter = new Location(world, 0, FLOOR_Y, 0);
            ArenaChunkLoader.releaseArena(world, floorCenter, ARENA_RADIUS + 4);
        }
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
        creator.generator(new ObservatoryChunkGenerator(ARENA_RADIUS, FLOOR_Y));
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

    private static String formatLocation(Location location) {
        return location.getWorld().getName()
            + " ("
            + location.getBlockX()
            + ", "
            + location.getBlockY()
            + ", "
            + location.getBlockZ()
            + ")";
    }
}
