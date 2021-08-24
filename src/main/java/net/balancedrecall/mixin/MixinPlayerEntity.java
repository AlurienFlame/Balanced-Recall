package net.balancedrecall.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.balancedrecall.BalancedRecall;
import net.balancedrecall.MatSleepingPlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements MatSleepingPlayer {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    // We make our own method because we can't use PlayerEntity.trySleep as it's overriden by
    // ServerEntity.trySleep, which sets spawn (we don't want that)
    @Shadow
    private int sleepTimer;
    
    @Override
    public void sleepOnMat(BlockPos pos) {
        super.sleep(pos);
        this.sleepTimer = 0;
    }
    
    // Interrupt magic mirror usage when taking damage

    @Shadow
    public ItemCooldownManager getItemCooldownManager() {
        System.out.println("This should never run");
		return new ItemCooldownManager();
	}


    @Inject(method = "applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", at = @At("HEAD"))
    protected void applyDamage(DamageSource source, float amount, CallbackInfo ci) {
        if (!this.isInvulnerableTo(source)) {
            // FIXME: Stops player from *starting* to use the magic mirror, but doesn't interrupt usage
            this.getItemCooldownManager().set(BalancedRecall.MAGIC_MIRROR, 20);
            this.getItemCooldownManager().set(BalancedRecall.DIMENSIONAL_MIRROR, 20);
        }
    }
}