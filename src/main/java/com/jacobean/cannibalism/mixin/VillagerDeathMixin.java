package com.jacobean.cannibalism.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public class VillagerDeathMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onVillagerDeath(DamageSource damageSource, CallbackInfo ci) {
        Entity attacker = damageSource.getAttacker();
        if (!(attacker instanceof PlayerEntity player)) return;

        VillagerEntity villager = (VillagerEntity) (Object) this;
        if (villager.getWorld().isClient()) return;

        Random random = villager.getRandom();
        int dropCount = 1 + random.nextInt(3); // 1-3 pieces

        ItemStack stack = new ItemStack(Items.ROTTEN_FLESH, dropCount);
        ItemEntity itemEntity = new ItemEntity(
                villager.getWorld(),
                villager.getX(),
                villager.getY(),
                villager.getZ(),
                stack
        );
        villager.getWorld().spawnEntity(itemEntity);
    }
}