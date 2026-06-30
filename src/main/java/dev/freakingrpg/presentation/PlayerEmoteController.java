package dev.freakingrpg.presentation;

import org.bukkit.entity.Player;

/**
 * Player-facing emotes and weapon swings. A plain Paper plugin cannot retarget the
 * vanilla player skeleton. Options:
 * <ul>
 *   <li>ModelEngine player rig (recommended for legendary weapon swings)</li>
 *   <li>Resource-pack CustomModelData for held items (first-person only, limited)</li>
 *   <li>Camera lock + VFX layer while hiding/mounting a modeled armor stand (cinematic)</li>
 * </ul>
 */
public interface PlayerEmoteController {

    boolean playEmote(Player player, String emoteId);

    boolean playWeaponSkill(Player player, String weaponId, String skillId);

    void stop(Player player);

    boolean isAvailable();

    static PlayerEmoteController noop() {
        return new PlayerEmoteController() {
            @Override
            public boolean playEmote(Player player, String emoteId) {
                return false;
            }

            @Override
            public boolean playWeaponSkill(Player player, String weaponId, String skillId) {
                return false;
            }

            @Override
            public void stop(Player player) {
            }

            @Override
            public boolean isAvailable() {
                return false;
            }
        };
    }
}
