package net.balancedrecall;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SleepingMat extends Item {
    public static final MutableComponent USER_DEAD = Component.translatable("item.balancedrecall.sleeping_mat.user_dead");
	public static final MutableComponent ALREADY_ASLEEP = Component.translatable("item.balancedrecall.sleeping_mat.already_asleep");
	public static final MutableComponent WRONG_DIMENSION = Component.translatable("item.balancedrecall.sleeping_mat.wrong_dimension");
	public static final MutableComponent NOT_POSSIBLE = Component.translatable("sleep.not_possible");
    public static final Component NOT_POSSIBLE_NOW = Player.BedSleepingProblem.NOT_POSSIBLE_NOW.getMessage();
    public static final Component NOT_SAFE = Player.BedSleepingProblem.NOT_SAFE.getMessage();

    SleepingMat(net.minecraft.world.item.Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        // ServerPlayerEntity.trySleep is where spawn is set, we need to circumvent it while taking advantage of as much vanilla code as possible
        // We also need to make sure that time since last sleep doesn't get reset - I don't want the sleeping mat to stop phantoms from spawning

        if (!(world instanceof ServerLevel)) {
            // Running on the client (bad)
            return InteractionResult.PASS;
        }
        ServerPlayer serverPlayer = (ServerPlayer) user;

        if (!serverPlayer.isAlive()) {
            // User is dead
            serverPlayer.displayClientMessage(USER_DEAD, false);
            return InteractionResult.PASS;

        } else if (serverPlayer.isSleeping()) {
            // User is already sleeping
            serverPlayer.displayClientMessage(ALREADY_ASLEEP, false);
            return InteractionResult.PASS;

        } else if (!world.dimensionType().natural()) {
            // Wrong dimension
            serverPlayer.displayClientMessage(WRONG_DIMENSION, false);
            return InteractionResult.PASS;

        } else if (world.isDay()) {
            // It's daytime
            serverPlayer.displayClientMessage(NOT_POSSIBLE_NOW, false);
            return InteractionResult.PASS;

        } else if (!serverPlayer.isCreative()) {
            // Hostile entities too close
            Vec3 pos = serverPlayer.position();
            List<Monster> list = world.getEntitiesOfClass(Monster.class, new AABB(pos.x() - 8.0D, pos.y() - 5.0D, pos.z() - 8.0D, pos.x() + 8.0D, pos.y() + 5.0D, pos.z() + 8.0D), (hostileEntity) -> {
                return hostileEntity.isPreventingPlayerRest(serverPlayer.serverLevel(), serverPlayer);
            });
            if (!list.isEmpty()) {
                serverPlayer.displayClientMessage(NOT_SAFE, false);
                return InteractionResult.PASS;
            }
        }

        // Go to sleep
        ((MatSleepingPlayer) serverPlayer).sleepOnMat(serverPlayer.blockPosition());

        // Skip the night
        if (!((ServerPlayer) serverPlayer).serverLevel().canSleepThroughNights()) {
            serverPlayer.displayClientMessage(NOT_POSSIBLE, false);
        }
        ((ServerLevel) world).updateSleepingPlayerList();

        // Update statistics
        serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        serverPlayer.awardStat(BalancedRecall.MAT_SLEEPS);
        if (BalancedRecall.config.getBoolean("sleeping_mat_resets_phantom_timer")) {
            serverPlayer.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        }

        // Damage durability
        // BUG: When used on 1 durability, item breaks, but player still tries to sleep briefly before cancelling
        // This is caused by the way we check if sleeping is possible: checking if the player is holding a sleeping mat.
        // Because the player stops holding the mat when it breaks, that interrupts the sleep.
        stack.hurtAndBreak(1, serverPlayer, LivingEntity.getSlotForHand(serverPlayer.getUsedItemHand()));

        return InteractionResult.CONSUME;
    }

    // For checking that player is sleeping on a mat, instead of just sleeping randomly for no reason
    public static boolean isHoldingSleepingMat(LivingEntity user) {
        // This is called every tick, so don't waste time.
        if (user.getPose() == Pose.SLEEPING || user.isSleeping()) { 
            // Check both hands for a mat
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = user.getItemInHand(hand);
                if (stack.getItem() == BalancedRecall.SLEEPING_MAT) {
                    return true;
                }
            }
        }
        return false;
    }
}