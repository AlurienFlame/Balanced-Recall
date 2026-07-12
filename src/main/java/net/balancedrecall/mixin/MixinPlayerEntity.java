package net.balancedrecall.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.balancedrecall.BalancedRecall;
import net.balancedrecall.MatSleepingPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity implements MatSleepingPlayer {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    // We make our own method because we can't use PlayerEntity.trySleep as it's overriden by
    // ServerEntity.trySleep, which sets spawn (we don't want that)
    @Shadow
    private int sleepTimer;

    @Override
    public void sleepOnMat(BlockPos pos) {
        super.startSleeping(pos);
        this.sleepTimer = 0;
    }

    // This only exists to make the compiler stop whining, it should never run
    @Shadow
    public abstract ItemCooldowns getItemCooldownManager();

    // Interrupt magic mirror usage when taking damage
    @Inject(method = "applyDamage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)V", at = @At("HEAD"))
    protected void applyDamage(ServerLevel world, DamageSource source, float amount, CallbackInfo info) {
        if (BalancedRecall.config.getBoolean("take_damage_interrupts_recall") && !this.isInvulnerableTo(world, source)) {
            // Interrupt usage
            ItemStack stack = this.getUseItem();
            if (stack.getItem() == BalancedRecall.MAGIC_MIRROR || stack.getItem() == BalancedRecall.DIMENSIONAL_MIRROR) {
                this.releaseUsingItem();
                if (BalancedRecall.config.getBoolean("take_damage_puts_mirror_on_cooldown")) {
                    // Start cooldown
                    this.getItemCooldownManager().addCooldown(stack, stack.getItem().components().get(DataComponents.USE_COOLDOWN).ticks());
                }
            }

        }
    }
}