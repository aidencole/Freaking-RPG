package dev.freakingrpg.presentation;

import dev.freakingrpg.FreakingRpgPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Resolves ModelEngine at runtime when installed. Until then, attacks rely on
 * display-entity VFX while the boss body stays a vanilla placeholder mob.
 */
public final class PresentationServices {

    private final BossAnimator bossAnimator;
    private final PlayerEmoteController playerEmotes;

    public PresentationServices(FreakingRpgPlugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) {
            this.bossAnimator = new ModelEngineBossAnimator(plugin);
            this.playerEmotes = new ModelEnginePlayerEmotes(plugin);
        } else {
            this.bossAnimator = BossAnimator.noop();
            this.playerEmotes = PlayerEmoteController.noop();
            plugin.getLogger().info(
                "ModelEngine not found — boss VFX will use BlockDisplay layers only. "
                    + "Install ModelEngine + resource pack for AAA models/animations."
            );
        }
    }

    public BossAnimator bosses() {
        return bossAnimator;
    }

    public PlayerEmoteController players() {
        return playerEmotes;
    }

    private static final class ModelEngineBossAnimator implements BossAnimator {
        private final FreakingRpgPlugin plugin;

        private ModelEngineBossAnimator(FreakingRpgPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void play(Entity entity, String animationId, AnimationMode mode) {
            // Hook point: call ModelEngine API once dependency is added to compile classpath.
            plugin.getLogger().fine("ModelEngine play " + animationId + " on " + entity.getUniqueId());
        }

        @Override
        public void stop(Entity entity, String animationId) {
            plugin.getLogger().fine("ModelEngine stop " + animationId + " on " + entity.getUniqueId());
        }

        @Override
        public boolean isAvailable() {
            return true;
        }
    }

    private static final class ModelEnginePlayerEmotes implements PlayerEmoteController {
        private final FreakingRpgPlugin plugin;

        private ModelEnginePlayerEmotes(FreakingRpgPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean playEmote(Player player, String emoteId) {
            plugin.getLogger().fine("ModelEngine emote " + emoteId + " for " + player.getName());
            return false;
        }

        @Override
        public boolean playWeaponSkill(Player player, String weaponId, String skillId) {
            plugin.getLogger().fine("ModelEngine weapon skill " + weaponId + ":" + skillId);
            return false;
        }

        @Override
        public void stop(Player player) {
        }

        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}
