package com.jacobean.cannibalism;

import com.jacobean.cannibalism.power.StackingHeartsPower;
import com.jacobean.cannibalism.util.OriginHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class ModEvents {

    private static final int SCAN_INTERVAL = 20;
    private static final double AGGRO_RANGE = 16.0;

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            StackingHeartsPower.restoreOnLogin(handler.player);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            StackingHeartsPower.restoreOnLogin(newPlayer);
        });

        ServerTickEvents.END_WORLD_TICK.register(ModEvents::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        if (world.getTime() % SCAN_INTERVAL != 0) return;

        for (PlayerEntity player : world.getPlayers()) {
            if (!OriginHelper.isCannibal(player)) continue;

            List<LivingEntity> nearbyMobs = world.getEntitiesByClass(
                    LivingEntity.class,
                    player.getBoundingBox().expand(AGGRO_RANGE),
                    e -> isNeutralMob(e) && e.isAlive()
            );

            for (LivingEntity entity : nearbyMobs) {
                if (entity instanceof MobEntity mob) {
                    if (mob.getTarget() == null) {
                        mob.setTarget(player);
                    }
                }
            }
        }
    }

    private static boolean isNeutralMob(LivingEntity entity) {
        return entity instanceof IronGolemEntity
                || entity instanceof PiglinEntity
                || entity instanceof ZombifiedPiglinEntity
                || entity instanceof EndermanEntity
                || entity instanceof WolfEntity
                || entity instanceof BeeEntity
                || entity instanceof PolarBearEntity;
    }
}