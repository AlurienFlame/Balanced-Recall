package net.balancedrecall;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
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
    public static final Component NOT_POSSIBLE_NOW = Component.translatable("block.minecraft.bed.no_sleep");
    public static final Component NOT_SAFE = Player.BedSleepingProblem.NOT_SAFE.message();

    SleepingMat(net.minecraft.world.item.Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        // TODO: rename to align with mojmap
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
            serverPlayer.sendOverlayMessage(USER_DEAD);
            return InteractionResult.PASS;

        }

        if (serverPlayer.isSleeping()) {
            // User is already sleeping
            serverPlayer.sendOverlayMessage(ALREADY_ASLEEP);
            return InteractionResult.PASS;

        }

        // check bedrules
        Vec3 pos = serverPlayer.position();
        BedRule rule = world.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, pos);
        
        if (rule.explodes()) {
            // User is in the correct dimension
            serverPlayer.sendOverlayMessage(WRONG_DIMENSION);
            return InteractionResult.PASS;
        }
        
        if (!rule.canSleep(world)) {
            // It's daytime in the correct dimension (no message if wrong dim)
            serverPlayer.sendOverlayMessage(rule.asProblem().message());
            return InteractionResult.PASS;
        }

        if (!serverPlayer.isCreative()) {
            // Hostile entities too close
            List<Monster> list = world.getEntitiesOfClass(Monster.class, new AABB(pos.x() - 8.0D, pos.y() - 5.0D, pos.z() - 8.0D, pos.x() + 8.0D, pos.y() + 5.0D, pos.z() + 8.0D), (hostileEntity) -> {
                return hostileEntity.isPreventingPlayerRest(serverPlayer.level(), serverPlayer);
            });
            if (!list.isEmpty()) {
                serverPlayer.sendOverlayMessage(NOT_SAFE);
                return InteractionResult.PASS;
            }
        }

        // Go to sleep
        ((MatSleepingPlayer) serverPlayer).sleepOnMat(serverPlayer.blockPosition());

        // Skip the night
        if (!((ServerPlayer) serverPlayer).level().canSleepThroughNights()) {
            serverPlayer.sendOverlayMessage(NOT_POSSIBLE);
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
        stack.hurtAndBreak(1, serverPlayer, serverPlayer.getUsedItemHand().asEquipmentSlot());

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