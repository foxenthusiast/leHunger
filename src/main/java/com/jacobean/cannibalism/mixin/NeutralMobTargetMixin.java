package com.jacobean.cannibalism.mixin;

import com.jacobean.cannibalism.util.OriginHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class NeutralMobTargetMixin {

    /**
     * canTarget is defined on LivingEntity in 1.21.1 yarn mappings, not MobEntity.
     * We inject here and then check if "this" is actually one of our outcast mob types.
     *
     * require = 0 means the game won't crash if the method signature doesn't match —
     * the mixin just silently skips instead. Useful as a safety net.
     */
    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true, require = 0)
    private void onCanTarget(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!(target instanceof PlayerEntity player)) return;
        if (!OriginHelper.isCannibal(player)) return;

        if (isOutcastMob((LivingEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }

    private boolean isOutcastMob(LivingEntity entity) {
        String className = entity.getClass().getSimpleName();
        return switch (className) {
            case "IronGolemEntity",
                 "PiglinEntity",
                 "ZombifiedPiglinEntity",
                 "EndermanEntity",
                 "WolfEntity",
                 "BeeEntity",
                 "PolarBearEntity" -> true;
            default -> false;
        };
    }
}