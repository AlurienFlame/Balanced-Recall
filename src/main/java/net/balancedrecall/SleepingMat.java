package net.balancedrecall;

import java.util.List;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class SleepingMat extends Item {
    public static final TranslatableText USER_DEAD = new TranslatableText("item.balancedrecall.sleeping_mat.user_dead");
	public static final TranslatableText ALREADY_ASLEEP = new TranslatableText("item.balancedrecall.sleeping_mat.already_asleep");
	public static final TranslatableText WRONG_DIMENSION = new TranslatableText("item.balancedrecall.sleeping_mat.wrong_dimension");
	public static final TranslatableText NOT_POSSIBLE = new TranslatableText("sleep.not_possible");
    public static final Text NOT_POSSIBLE_NOW = PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW.toText();
    public static final Text NOT_SAFE = PlayerEntity.SleepFailureReason.NOT_SAFE.toText();

    SleepingMat(Settings settings) {
        super(settings);
    }

    // TODO: Figure out the difference between sendSystemMessage and sendMessage, and possibly swap them out

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
            user.sendSystemMessage(USER_DEAD, Util.NIL_UUID);
            return TypedActionResult.pass(stack);

        } else if (user.isSleeping()) {
            // User is already sleeping
            user.sendSystemMessage(ALREADY_ASLEEP, Util.NIL_UUID);
            return TypedActionResult.pass(stack);

        } else if (!world.getDimension().isNatural()) {
            // Wrong dimension
            user.sendSystemMessage(WRONG_DIMENSION, Util.NIL_UUID);
            return TypedActionResult.pass(stack);

        } else if (world.isDay()) {
            // It's daytime
            user.sendSystemMessage(NOT_POSSIBLE_NOW, Util.NIL_UUID);
            return TypedActionResult.pass(stack);
            
        } else if (!user.isCreative()) {
            // Hostile entities too close
            Vec3d pos = user.getPos();
            List<HostileEntity> list = world.getEntitiesByClass(HostileEntity.class, new Box(pos.getX() - 8.0D, pos.getY() - 5.0D, pos.getZ() - 8.0D, pos.getX() + 8.0D, pos.getY() + 5.0D, pos.getZ() + 8.0D), (hostileEntity) -> {
                return hostileEntity.isAngryAt(user);
            });
            if (!list.isEmpty()) {
                user.sendSystemMessage(NOT_SAFE, Util.NIL_UUID);
                return TypedActionResult.pass(stack);
            }
        }

        // TODO: Figure out if this is resetting phantom timer or not
        // TODO: Set statistics and advancements
        // Statistics to set: Times item used, times slept on sleeping mat
        // Statistics to NOT set: Time since last slept, times slept in bed, phantom timer

        // Go to sleep
        ((MatSleepingPlayer) user).sleepOnMat(user.getBlockPos());

        // Skip the night
        if (!((ServerPlayerEntity) user).getServerWorld().isSleepingEnabled()) {
            user.sendSystemMessage(NOT_POSSIBLE, Util.NIL_UUID);
        }
        ((ServerWorld) world).updateSleepingPlayers();
        
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