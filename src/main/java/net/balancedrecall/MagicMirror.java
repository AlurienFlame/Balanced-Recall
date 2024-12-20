package net.balancedrecall;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public class MagicMirror extends Item {
    boolean isInterdimensional;

    public MagicMirror(net.minecraft.item.Item.Settings settings) {
        super(settings);
        isInterdimensional = false;
    }

    // Can't be used on 1 or less durability
    public static boolean isUsable(ItemStack stack) {
		return stack.getDamage() < stack.getMaxDamage() - 1;
	}


    @Override
    public ActionResult use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);
        if (MagicMirror.isUsable(stack)) {
            // Use item
            return ItemUsage.consumeHeldItem(world, playerEntity, hand);
        } else {
            // Item is out of durability, don't use it
            return ActionResult.PASS;
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // finishUsing runs on both the server and the client (for some reason),
        // but we only want to run this code on the server.
        if (!(world instanceof ServerWorld)) {
            return stack;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;
        ServerWorld targetWorld = serverPlayer.server.getWorld(serverPlayer.getSpawnPointDimension());

        if ( !isInterdimensional && serverPlayer.getWorld() != targetWorld) {
            // This mirror is too weak to cross the veil between worlds! Maybe a rare nether metal could help...
            serverPlayer.sendMessage(Text.translatable("balancedrecall.fail_cross_dimension"), false);
            return stack;
        }
        serverPlayer.teleportTo(serverPlayer.getRespawnTarget(false, TeleportTarget.NO_OP));
        targetWorld.playSound(null, serverPlayer.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);

        // Update statistics
        serverPlayer.incrementStat(BalancedRecall.RECALLS);
        serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));

        // Damage durability
        stack.damage(1, (LivingEntity)serverPlayer, LivingEntity.getSlotForHand(serverPlayer.getActiveHand()));

        // Put on cooldown
        serverPlayer.getItemCooldownManager().set(stack, stack.getItem().getComponents().get(DataComponentTypes.USE_COOLDOWN).getCooldownTicks());

        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 20;
    }
}