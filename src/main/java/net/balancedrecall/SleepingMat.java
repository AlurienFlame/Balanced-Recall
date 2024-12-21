package net.balancedrecall;

import java.util.List;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
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

    SleepingMat(net.minecraft.item.Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // ServerPlayerEntity.trySleep is where spawn is set, we need to circumvent it while taking advantage of as much vanilla code as possible
        // We also need to make sure that time since last sleep doesn't get reset - I don't want the sleeping mat to stop phantoms from spawning

        if (!(world instanceof ServerWorld)) {
            // Running on the client (bad)
            return ActionResult.PASS;
        }
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;

        if (!serverPlayer.isAlive()) {
            // User is dead
            serverPlayer.sendMessage(USER_DEAD, false);
            return ActionResult.PASS;

        } else if (serverPlayer.isSleeping()) {
            // User is already sleeping
            serverPlayer.sendMessage(ALREADY_ASLEEP, false);
            return ActionResult.PASS;

        } else if (!world.getDimension().natural()) {
            // Wrong dimension
            serverPlayer.sendMessage(WRONG_DIMENSION, false);
            return ActionResult.PASS;

        } else if (world.isDay()) {
            // It's daytime
            serverPlayer.sendMessage(NOT_POSSIBLE_NOW, false);
            return ActionResult.PASS;

        } else if (!serverPlayer.isCreative()) {
            // Hostile entities too close
            Vec3d pos = serverPlayer.getPos();
            List<HostileEntity> list = world.getEntitiesByClass(HostileEntity.class, new Box(pos.getX() - 8.0D, pos.getY() - 5.0D, pos.getZ() - 8.0D, pos.getX() + 8.0D, pos.getY() + 5.0D, pos.getZ() + 8.0D), (hostileEntity) -> {
                return hostileEntity.isAngryAt(serverPlayer.getServerWorld(), serverPlayer);
            });
            if (!list.isEmpty()) {
                serverPlayer.sendMessage(NOT_SAFE, false);
                return ActionResult.PASS;
            }
        }

        // Go to sleep
        ((MatSleepingPlayer) serverPlayer).sleepOnMat(serverPlayer.getBlockPos());

        // Skip the night
        if (!((ServerPlayerEntity) serverPlayer).getServerWorld().isSleepingEnabled()) {
            serverPlayer.sendMessage(NOT_POSSIBLE, false);
        }
        ((ServerWorld) world).updateSleepingPlayers();

        // Update statistics
        serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
        serverPlayer.incrementStat(BalancedRecall.MAT_SLEEPS);
        if (BalancedRecall.config.getBoolean("sleeping_mat_resets_phantom_timer")) {
            serverPlayer.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        }

        // Damage durability
        // BUG: When used on 1 durability, item breaks, but player still tries to sleep briefly before cancelling
        // This is caused by the way we check if sleeping is possible: checking if the player is holding a sleeping mat.
        // Because the player stops holding the mat when it breaks, that interrupts the sleep.
        stack.damage(1, serverPlayer, LivingEntity.getSlotForHand(serverPlayer.getActiveHand()));

        return ActionResult.CONSUME;
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