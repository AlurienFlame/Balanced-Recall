package net.balancedrecall;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;

public class SleepingMat extends Item {
    public static final MutableText USER_DEAD = Text.translatable("item.balancedrecall.sleeping_mat.user_dead");
	public static final MutableText ALREADY_ASLEEP = Text.translatable("item.balancedrecall.sleeping_mat.already_asleep");
	public static final MutableText WRONG_DIMENSION = Text.translatable("item.balancedrecall.sleeping_mat.wrong_dimension");
	public static final MutableText NOT_POSSIBLE = Text.translatable("sleep.not_possible");
    public static final Text NOT_POSSIBLE_NOW = PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW.getMessage();
    public static final Text NOT_SAFE = PlayerEntity.SleepFailureReason.NOT_SAFE.getMessage();

    SleepingMat(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // ServerPlayerEntity.trySleep is where spawn is set, we need to circumvent it while taking advantage of as much vanilla code as possible
        // We also need to make sure that time since last sleep doesn't get reset - I don't want the sleeping mat to stop phantoms from spawning

        if (world.isClient) {
            // Running on the client (bad)
            return TypedActionResult.pass(stack);
        } else if (!user.isAlive()) {
            // User is dead
            user.sendMessage(USER_DEAD, false);
            return TypedActionResult.pass(stack);

        } else if (user.isSleeping()) {
            // User is already sleeping
            user.sendMessage(ALREADY_ASLEEP, false);
            return TypedActionResult.pass(stack);

        } else if (!world.getDimension().natural()) {
            // Wrong dimension
            user.sendMessage(WRONG_DIMENSION, false);
            return TypedActionResult.pass(stack);

        } else if (world.isDay()) {
            // It's daytime
            user.sendMessage(NOT_POSSIBLE_NOW, false);
            return TypedActionResult.pass(stack);
            
        } else if (!user.isCreative()) {
            // Hostile entities too close
            Vec3d pos = user.getPos();
            List<HostileEntity> list = world.getEntitiesByClass(HostileEntity.class, new Box(pos.getX() - 8.0D, pos.getY() - 5.0D, pos.getZ() - 8.0D, pos.getX() + 8.0D, pos.getY() + 5.0D, pos.getZ() + 8.0D), (hostileEntity) -> {
                return hostileEntity.isAngryAt(user);
            });
            if (!list.isEmpty()) {
                user.sendMessage(NOT_SAFE, false);
                return TypedActionResult.pass(stack);
            }
        }

        // Go to sleep
        ((MatSleepingPlayer) user).sleepOnMat(user.getBlockPos());
        
        // Skip the night
        if (!((ServerPlayerEntity) user).getServerWorld().isSleepingEnabled()) {
            user.sendMessage(NOT_POSSIBLE, false);
        }
        ((ServerWorld) world).updateSleepingPlayers();
        
        // Update statistics
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        user.incrementStat(BalancedRecall.MAT_SLEEPS);
        
        // Damage durability
        stack.damage(1, user, (Consumer<LivingEntity>)((p) -> {
            p.sendToolBreakStatus(user.getActiveHand());
        }));
        
        return TypedActionResult.consume(stack);
    }

    // For checking that player is sleeping on a mat, instead of just sleeping randomly for no reason
    public static boolean isHoldingSleepingMat(LivingEntity user) {
        // This is called every tick, so don't waste time.
        if (user.getPose() == EntityPose.SLEEPING || user.isSleeping()) { 
            // Check both hands for a mat
            for (Hand hand : Hand.values()) {
                ItemStack stack = user.getStackInHand(hand);
                if (stack.getItem() == BalancedRecall.SLEEPING_MAT) {
                    return true;
                }
            }
        }
        return false;
    }
}