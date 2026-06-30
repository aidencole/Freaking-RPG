package dev.freakingrpg.vfx;

import dev.freakingrpg.FreakingRpgPlugin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public final class VfxRunner {

    private final FreakingRpgPlugin plugin;
    private final List<Entity> trackedEntities = new CopyOnWriteArrayList<>();
    private final List<ScheduledTask> trackedTasks = new CopyOnWriteArrayList<>();

    public VfxRunner(FreakingRpgPlugin plugin) {
        this.plugin = plugin;
    }

    public void track(Entity entity, int lifeTicks) {
        trackedEntities.add(entity);
        entity.getScheduler().runDelayed(plugin, task -> discard(entity), null, lifeTicks);
    }

    public void trackTask(ScheduledTask task) {
        trackedTasks.add(task);
    }

    public void launchRock(
        BlockDisplay display,
        Vector velocity,
        int lifeTicks,
        float gravity
    ) {
        track(display, lifeTicks);
        Transformation base = display.getTransformation();
        Vector3f translation = new Vector3f(base.getTranslation());

        ScheduledTask task = display.getScheduler().runAtFixedRate(plugin, scheduled -> {
            if (!display.isValid()) {
                scheduled.cancel();
                return;
            }

            translation.add(
                (float) velocity.getX(),
                (float) velocity.getY(),
                (float) velocity.getZ()
            );
            velocity.setY(velocity.getY() - gravity);

            display.setTransformation(new Transformation(
                translation,
                base.getLeftRotation(),
                base.getScale(),
                base.getRightRotation()
            ));
            display.setInterpolationDuration(1);
        }, null, 1L, 1L);

        trackTask(task);
        display.getScheduler().runDelayed(plugin, scheduled -> {
            scheduled.cancel();
            discard(display);
        }, null, lifeTicks);
    }

    public void discard(Entity entity) {
        trackedEntities.remove(entity);
        if (entity.isValid()) {
            entity.remove();
        }
    }

    public void shutdown() {
        for (ScheduledTask task : trackedTasks) {
            task.cancel();
        }
        trackedTasks.clear();

        for (Entity entity : new ArrayList<>(trackedEntities)) {
            discard(entity);
        }
    }
}
