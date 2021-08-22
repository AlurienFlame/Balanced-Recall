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

    @Override
    public void sleepOnMat(BlockPos pos) {
        super.sleep(pos);
		this.sleepTimer = 0;
        System.out.println("Sleeping on mat.");
    }
}