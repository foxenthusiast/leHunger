package com.jacobean.cannibalism.power;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class StackingHeartsPower {

    private static final double HEALTH_PER_FLESH = 2.0;
    private static final Identifier MODIFIER_ID = Identifier.of("jacobean-cannibalism", "flesh_bonus");
    private static final String NBT_KEY = "jacobean_cannibalism_flesh_eaten";

    /**
     * Called when a Cannibal player eats rotten flesh.
     * We store the count in the player's custom NBT data — NBT is
     * Minecraft's format for saving arbitrary data to entities/items.
     * Using getNbt() gives us a compound tag we can read/write freely.
     */
    public static void onFleshEaten(PlayerEntity player) {
        int fleshEaten = readFleshCount(player) + 1;
        writeFleshCount(player, fleshEaten);
        applyHealthBoost(player, fleshEaten);
    }

    /**
     * Applies a permanent max health boost based on total flesh eaten.
     * Removes the old modifier first so values don't stack up incorrectly.
     */
    public static void applyHealthBoost(PlayerEntity player, int fleshEaten) {
        EntityAttributeInstance maxHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (maxHealth == null) return;

        maxHealth.removeModifier(MODIFIER_ID);

        if (fleshEaten > 0) {
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    MODIFIER_ID,
                    fleshEaten * HEALTH_PER_FLESH,
                    EntityAttributeModifier.Operation.ADD_VALUE
            );
            maxHealth.addPersistentModifier(modifier);
        }
    }

    private static int readFleshCount(PlayerEntity player) {
        NbtCompound nbt = player.writeNbt(new NbtCompound());
        return nbt.contains(NBT_KEY) ? nbt.getInt(NBT_KEY) : 0;
    }

    private static void writeFleshCount(PlayerEntity player, int count) {
        // We use a persistent data component via the player's nbt
        NbtCompound nbt = new NbtCompound();
        nbt.putInt(NBT_KEY, count);
        player.readNbt(nbt);
    }
}