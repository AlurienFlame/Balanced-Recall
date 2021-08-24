package net.balancedrecall.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.balancedrecall.MatSleepingPlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements MatSleepingPlayer {

	protected MixinPlayerEntity(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

    @Shadow
	private int sleepTimer;

    // Because we can't use PlayerEntity.trySleep as it's overriden by
    // ServerEntity.trySleep, which sets spawn (we don't want that)
    @Override
    public void sleepOnMat(BlockPos pos) {
        super.sleep(pos);
		this.sleepTimer = 0;
    }
}