package com.jacobean.cannibalism;

import com.jacobean.cannibalism.power.StackingHeartsPower;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class ModEvents {

    /**
     * Register server-side event callbacks.
     *
     * ServerPlayConnectionEvents.JOIN fires when a player connects to the server.
     * ServerPlayerEvents.AFTER_RESPAWN fires after a player respawns (death or
     * dimension change). Both cases need the boost reapplied because Minecraft
     * rebuilds the player entity from scratch in both situations, discarding
     * any in-memory attribute modifiers that weren't saved as "persistent".
     *
     * The (oldPlayer, newPlayer, alive) parameters on AFTER_RESPAWN let you
     * copy data from the old entity to the new one — we don't need that here
     * since restoreOnLogin reads from the attachment (which IS copied over),
     * but it's useful to know about for future reference.
     */
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            StackingHeartsPower.restoreOnLogin(handler.player);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            StackingHeartsPower.restoreOnLogin(newPlayer);
        });
    }
}