package net.balancedrecall.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.balancedrecall.SleepingMat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    // Convince the game that using a sleeping mat counts as sleeping in a bed
    @Inject(method = "isSleepingInBed", at = @At("HEAD"), cancellable = true)
    protected void isSleepingInBed(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (SleepingMat.isHoldingSleepingMat((LivingEntity) (Entity) this)) {
            callbackInfo.setReturnValue(true);
        }
    }
}
