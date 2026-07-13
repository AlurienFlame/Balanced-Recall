package net.balancedrecall;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData.RespawnData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.mojang.datafixers.util.Either;

public class MagicMirror extends Item {
    boolean isInterdimensional;

    public MagicMirror(net.minecraft.world.item.Item.Properties settings) {
        super(settings);
        isInterdimensional = false;
    }

    // Can't be used on 1 or less durability
    public static boolean isUsable(ItemStack stack) {
		return stack.getDamageValue() < stack.getMaxDamage() - 1;
	}


    @Override
    public InteractionResult use(Level world, Player playerEntity, InteractionHand hand) {
        ItemStack stack = playerEntity.getItemInHand(hand);
        if (MagicMirror.isUsable(stack)) {
            // Use item
            return ItemUtils.startUsingInstantly(world, playerEntity, hand);
        } else {
            // Item is out of durability, don't use it
            return InteractionResult.PASS;
        }
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        // finishUsing runs on both the server and the client (for some reason),
        // but we only want to run this code on the server.
        if (!(world instanceof ServerLevel)) {
            return stack;
        }
        ServerLevel serverLevel = (ServerLevel) world;

        ServerPlayer serverPlayer = (ServerPlayer) user;

        RespawnData respawnData = serverPlayer.getRespawnConfig().respawnData();
        if ( !isInterdimensional && respawnData.dimension() != world.dimension()) {
            // This mirror is too weak to cross the veil between worlds! Maybe a rare nether metal could help...
            serverPlayer.sendOverlayMessage(Component.translatable("balancedrecall.fail_cross_dimension"));
            return stack;
        }
        
        // Cannot teleport while monsters are nearby
        if (BalancedRecall.config.getBoolean("recall_impossible_when_monsters_nearby")) {
            Vec3 feet = Vec3.atBottomCenterOf(serverPlayer.blockPosition());
            List<Monster> list = serverPlayer.level()
                .getEntitiesOfClass(
                    Monster.class,
                    new AABB(feet.x() - 8.0, feet.y() - 5.0, feet.z() - 8.0, feet.x() + 8.0, feet.y() + 5.0, feet.z() + 8.0),
                    entity -> entity.isPreventingPlayerRest(serverLevel, serverPlayer)
                );
            if (!list.isEmpty()) {
                serverPlayer.sendOverlayMessage(Component.translatable("balancedrecall.fail_monsters_nearby"));
                return stack;
            }
        }

        TeleportTransition transition = serverPlayer.findRespawnPositionAndUseSpawnBlock(false, TeleportTransition.DO_NOTHING);
        serverPlayer.teleport(transition);

        // ServerLevel targetWorld = serverPlayer.level().getServer().getLevel(respawnData.dimension()); // TODO: get rid of after testing
        ServerLevel targetWorld = transition.newLevel();
        targetWorld.playSound(null, serverPlayer.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.4f, 1f);

        // Update statistics
        serverPlayer.awardStat(BalancedRecall.RECALLS);
        serverPlayer.awardStat(Stats.ITEM_USED.get(this));

        // Damage durability
        stack.hurtAndBreak(1, (LivingEntity)serverPlayer, serverPlayer.getUsedItemHand().asEquipmentSlot());

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return (int)(BalancedRecall.config.getDouble("magic_mirror_use_time_seconds") * 20);
    }
}