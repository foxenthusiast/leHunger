package com.jacobean.cannibalism;

public class ModPowers {

    /**
     * Called on mod init. Nothing to register here since our stacking
     * hearts power runs via a mixin rather than the Origins power registry.
     * We keep this class in case we add Origins-native powers later.
     */
    public static void register() {
        JacobeanCannibalism.LOGGER.info("Cannibal powers initialised.");
    }
}