package com.jacobean.cannibalism.power;

import com.jacobean.cannibalism.JacobeanCannibalism;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class StackingHeartsPower {

    private static final double HEALTH_PER_FLESH = 2.0;
    private static final Identifier MODIFIER_ID =
            Identifier.of("jacobean-cannibalism", "flesh_bonus");

    /**
     * An AttachmentType is Fabric's way of storing custom persistent data
     * directly on an entity. Think of it like a named slot you can read/write
     * on any entity, and Fabric handles saving it to disk automatically.
     *
     * The initializer () -> 0 means "if no data is stored yet, default to 0".
     * .persistent(codec) means the value survives world save/load.
     */
    public static final AttachmentType<Integer> FLESH_COUNT =
            AttachmentRegistry.createPersistent(
                    Identifier.of(JacobeanCannibalism.MOD_ID, "flesh_eaten"),
                    Codec.INT
            );

    /**
     * Called when a Cannibal player eats rotten flesh.
     * Increments the stored count and updates the health modifier.
     */
    public static void onFleshEaten(PlayerEntity player) {
        // getAttachedOrElse returns the stored value, or 0 if nothing stored yet
        int current = player.getAttachedOrElse(FLESH_COUNT, 0);
        int next = current + 1;
        player.setAttached(FLESH_COUNT, next);
        applyHealthBoost(player, next);
    }

    /**
     * Re-applies the max health modifier based on total flesh eaten.
     * We always remove the old modifier first so it doesn't double-stack.
     *
     * ADD_VALUE means: maxHealth = baseValue + (fleshEaten * 2.0)
     * Each heart = 2.0 health points in Minecraft's internal system.
     */
    public static void applyHealthBoost(PlayerEntity player, int fleshEaten) {
        EntityAttributeInstance maxHealth =
                player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (maxHealth == null) return;

        // Remove old modifier so we don't stack duplicates
        maxHealth.removeModifier(MODIFIER_ID);

        if (fleshEaten > 0) {
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    MODIFIER_ID,
                    fleshEaten * HEALTH_PER_FLESH,
                    EntityAttributeModifier.Operation.ADD_VALUE
            );
            // addPersistentModifier means it survives saves (unlike addTemporaryModifier)
            maxHealth.addPersistentModifier(modifier);
        }
    }

    /**
     * Call this when a player logs in or respawns to restore their bonus.
     * Without this, the attribute modifier is lost on relog even though
     * the flesh count is saved — we need to reapply it from the stored count.
     */
    public static void restoreOnLogin(PlayerEntity player) {
        int fleshEaten = player.getAttachedOrElse(FLESH_COUNT, 0);
        if (fleshEaten > 0) {
            applyHealthBoost(player, fleshEaten);
        }
    }
}