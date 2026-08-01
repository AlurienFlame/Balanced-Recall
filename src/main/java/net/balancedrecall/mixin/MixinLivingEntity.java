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

    // Convince the game that using a sleeping mat counts as sleeping in a bed.
    // Without this, LivingEntity.tick sees that the sleeping pos isn't a BedBlock and
    // wakes the player back up a tick later, so sleepCounter never reaches SLEEP_DURATION
    // and the night is never skipped.
    @Inject(method = "checkBedExists()Z", at = @At("HEAD"), cancellable = true)
    private void checkBedExists(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (SleepingMat.isHoldingSleepingMat((LivingEntity) (Entity) this)) {
            callbackInfo.setReturnValue(true);
        }
    }
}
