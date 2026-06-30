package dev.freakingrpg.boss;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class BossKeys {

    private final NamespacedKey instanceId;

    public BossKeys(Plugin plugin) {
        this.instanceId = new NamespacedKey(plugin, "boss_instance_id");
    }

    public NamespacedKey instanceId() {
        return instanceId;
    }
}
