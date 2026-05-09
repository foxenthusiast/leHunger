package com.jacobean.cannibalism.util;

import net.minecraft.entity.player.PlayerEntity;

import java.lang.reflect.Method;

public class OriginHelper {

    /**
     * We identify a Cannibal by checking if they have our marker power active.
     *
     * Pure reflection means we never import Origins classes directly, so this
     * compiles even if Origins isn't on the compile classpath. Reflection works
     * by looking up classes and methods by name as strings at runtime — slower
     * than direct calls but fine for an occasional check like this.
     *
     * The flow:
     * 1. Find the PowerHolderComponent class by name
     * 2. Get its static KEY field (a ComponentKey used to retrieve the component from a player)
     * 3. Call KEY.get(player) to get the component instance
     * 4. Call getPowers() on that component to get the list of active powers
     * 5. Check if any power's ID matches our marker
     */
    public static boolean isCannibal(PlayerEntity player) {
        try {
            // Step 1: load the class
            Class<?> componentClass = Class.forName(
                    "io.github.apoli.apoli.component.PowerHolderComponent"
            );

            // Step 2: get the KEY field (it's a ComponentKey<PowerHolderComponent>)
            var keyField = componentClass.getField("KEY");
            Object key = keyField.get(null); // null because it's static

            // Step 3: call key.get(player) — ComponentKey has a get(Provider) method
            Method getMethod = key.getClass().getMethod("get", Object.class);
            Object component = getMethod.invoke(key, player);

            // Step 4: call getPowers() — returns a Collection of Power instances
            // Try getPowers() first, then getPowers(null) as fallback
            Object powers;
            try {
                Method getPowers = component.getClass().getMethod("getPowers");
                powers = getPowers.invoke(component);
            } catch (NoSuchMethodException e) {
                Method getPowers = component.getClass().getMethod("getPowers", Class.class);
                powers = getPowers.invoke(component, (Object) null);
            }

            // Step 5: stream through and check IDs
            Iterable<?> powerList = (Iterable<?>) powers;
            String markerId = "jacobean-cannibalism:no_hunger_from_rotten_flesh";

            for (Object power : powerList) {
                // Powers have getId() or getType().getIdentifier() depending on version
                // Try getId() first
                try {
                    Method getId = power.getClass().getMethod("getId");
                    Object id = getId.invoke(power);
                    if (markerId.equals(id.toString())) return true;
                } catch (NoSuchMethodException e) {
                    Method getType = power.getClass().getMethod("getType");
                    Object type = getType.invoke(power);
                    Method getIdentifier = type.getClass().getMethod("getIdentifier");
                    Object id = getIdentifier.invoke(type);
                    if (markerId.equals(id.toString())) return true;
                }
            }

            return false;
        } catch (Exception e) {
            // Any failure (class not found, wrong version, etc.) = not a cannibal
            return false;
        }
    }
}