package com.jacobean.cannibalism.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class OriginHelper {

    private static final Identifier CANNIBAL_ORIGIN_ID =
            Identifier.of("jacobean-cannibalism", "cannibal");

    /**
     * Checks if the player has the Cannibal origin by reading the Origins
     * layer component via reflection. We look up the origin layer component,
     * get the current origin for the "origins:origin" layer, and check its ID.
     *
     * This avoids depending on Apoli's power list (which changed between
     * pre.1 and pre.2) and instead checks the origin ID directly, which
     * is stable.
     */
    public static boolean isCannibal(PlayerEntity player) {
        try {
            // Load the OriginComponent class
            Class<?> originComponentClass = Class.forName(
                    "io.github.apoli.apoli.component.OriginComponent"
            );

            // Get the KEY field (ComponentKey<OriginComponent>)
            var keyField = originComponentClass.getField("KEY");
            Object key = keyField.get(null);

            // Call key.get(player) to retrieve the component
            java.lang.reflect.Method getMethod =
                    key.getClass().getMethod("get", Object.class);
            Object component = getMethod.invoke(key, player);

            // Call getOrigin(layer) — we need to get the layer first
            // Layer is identified by Identifier "origins:origin"
            Class<?> registryClass = Class.forName(
                    "io.github.apoli.apoli.registry.ApoliRegistries"
            );
            var layerRegistryField = registryClass.getField("ORIGIN_LAYER");
            Object layerRegistry = layerRegistryField.get(null);

            // Get the layer from the registry
            java.lang.reflect.Method getLayerMethod =
                    layerRegistry.getClass().getMethod("get", Identifier.class);
            Object layer = getLayerMethod.invoke(
                    layerRegistry, Identifier.of("origins", "origin")
            );

            if (layer == null) return false;

            // Call component.getOrigin(layer)
            java.lang.reflect.Method getOriginMethod =
                    component.getClass().getMethod("getOrigin", layer.getClass());
            Object origin = getOriginMethod.invoke(component, layer);

            if (origin == null) return false;

            // Get the origin's identifier
            java.lang.reflect.Method getIdMethod =
                    origin.getClass().getMethod("getIdentifier");
            Object id = getIdMethod.invoke(origin);

            return CANNIBAL_ORIGIN_ID.equals(id);

        } catch (Exception e) {
            return false;
        }
    }
}