package com.jacobean.cannibalism.util;

import com.jacobean.cannibalism.JacobeanCannibalism;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class OriginHelper {

    private static final Identifier CANNIBAL_ORIGIN_ID =
            Identifier.of("jacobean-cannibalism", "cannibal");

    private static String foundClassName = null;
    private static boolean scanned = false;

    private static String findClass() {
        String[] candidates = {
                "io.github.apace100.apoli.component.OriginComponent",
                "io.github.apace100.apoli.component.PowerHolderComponent",
                "io.github.apoli.apoli.component.OriginComponent",
                "io.github.apoli.apoli.component.PowerHolderComponent",
        };
        for (String name : candidates) {
            try {
                Class.forName(name);
                JacobeanCannibalism.LOGGER.info("Found origin component class: {}", name);
                return name;
            } catch (ClassNotFoundException ignored) {}
        }
        JacobeanCannibalism.LOGGER.error("Could not find OriginComponent — none of the candidates exist");
        return null;
    }

    public static boolean isCannibal(PlayerEntity player) {
        try {
            if (!scanned) {
                scanned = true;
                foundClassName = findClass();
            }
            if (foundClassName == null) return false;

            Class<?> componentClass = Class.forName(foundClassName);
            var keyField = componentClass.getField("KEY");
            Object key = keyField.get(null);

            java.lang.reflect.Method getMethod = key.getClass().getMethod("get", Object.class);
            Object component = getMethod.invoke(key, player);

            // Try getOrigins() which returns Map<OriginLayer, Origin>
            java.lang.reflect.Method getOriginsMethod = component.getClass().getMethod("getOrigins");
            Object originsMap = getOriginsMethod.invoke(component);
            java.lang.reflect.Method valuesMethod = originsMap.getClass().getMethod("values");
            Iterable<?> origins = (Iterable<?>) valuesMethod.invoke(originsMap);

            for (Object origin : origins) {
                try {
                    Object id = origin.getClass().getMethod("getIdentifier").invoke(origin);
                    JacobeanCannibalism.LOGGER.info("Player {} origin: {}", player.getName().getString(), id);
                    if (CANNIBAL_ORIGIN_ID.equals(id)) return true;
                } catch (NoSuchMethodException e) {
                    Object id = origin.getClass().getMethod("getId").invoke(origin);
                    if (CANNIBAL_ORIGIN_ID.equals(id)) return true;
                }
            }
            return false;

        } catch (Exception e) {
            JacobeanCannibalism.LOGGER.error("isCannibal failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
}