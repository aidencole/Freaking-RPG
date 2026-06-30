package dev.freakingrpg.boss;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public final class BossListener implements Listener {

    private final BossManager bossManager;

    public BossListener(BossManager bossManager) {
        this.bossManager = bossManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBossDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity living)) {
            return;
        }

        bossManager.findByEntity(living.getUniqueId()).ifPresent(instance -> {
            if (instance.state() == BossFightState.TELEGRAPH || instance.state() == BossFightState.EXECUTE) {
                event.setDamage(event.getDamage() * 0.35);
            } else if (instance.isVulnerable()) {
                event.setDamage(event.getDamage() * 1.35);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBossDeath(EntityDeathEvent event) {
        bossManager.findByEntity(event.getEntity().getUniqueId()).ifPresent(instance -> {
            event.getDrops().clear();
            event.setDroppedExp(0);
            bossManager.onBossDefeated(instance);
        });
    }
}
