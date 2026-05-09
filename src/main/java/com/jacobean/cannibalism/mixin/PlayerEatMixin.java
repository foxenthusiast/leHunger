package com.jacobean.cannibalism.mixin;

import com.jacobean.cannibalism.power.StackingHeartsPower;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEatMixin {

    /**
     * In 1.21.1, eatFood has a third parameter — FoodComponent — which
     * describes the nutritional properties of what was eaten. Our mixin
     * signature must match exactly or the game crashes at startup.
     */
    @Inject(method = "eatFood", at = @At("RETURN"))
    private void onEatFood(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if (world.isClient()) return;
        if (!stack.isOf(Items.ROTTEN_FLESH)) return;

        PlayerEntity player = (PlayerEntity) (Object) this;
        StackingHeartsPower.onFleshEaten(player);
    }
}