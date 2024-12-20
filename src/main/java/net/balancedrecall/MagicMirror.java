package net.balancedrecall;

import java.util.Optional;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

// TODO: Trigger recipe unlock when player gets diamond, blue ice, or ender eye
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
        // TODO: Instead, wrap all logic that must be run only on the server side with if (world instanceof ServerWorld serverWorld). The serverWorld can be passed to those methods.
        // TODO: Add recipes to group
        if (world.isClient()) {
            return stack;
        }

        PlayerEntity player = (PlayerEntity) user;
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;
        ServerWorld targetWorld = serverPlayer.server.getWorld(serverPlayer.getSpawnPointDimension());

        BlockPos spawnpoint = serverPlayer.getSpawnPointPosition();

        if (spawnpoint != null) {
            // Player spawn

            // Find respawn position
            // PlayerEntity.findRespawnPosition exhausts respawn anchor charges, which is undesirable, so instead we replicate its functionality directly
            BlockState respawnBlockState = targetWorld.getBlockState(spawnpoint);
            Block respawnBlock = respawnBlockState.getBlock();
            Optional<Vec3d> respawnPosition = Optional.empty();

            if (respawnBlock instanceof RespawnAnchorBlock) {
                respawnPosition = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, targetWorld, spawnpoint);

            } else if (respawnBlock instanceof BedBlock) {
                respawnPosition = BedBlock.findWakeUpPosition(
                    EntityType.PLAYER,
                    targetWorld,
                    spawnpoint, 
                    respawnBlockState.get(BedBlock.FACING), 
                    serverPlayer.getSpawnAngle()
                );

            } else if (serverPlayer.isSpawnForced()){
                // Spawnpoint set by /spawnpoint command or equivalent
                boolean footBlockClear = respawnBlock.canMobSpawnInside(respawnBlockState);
			    boolean headBlockClear = targetWorld.getBlockState(spawnpoint.up()).getBlock().canMobSpawnInside(respawnBlockState);
			    if (footBlockClear && headBlockClear) {
                    respawnPosition = Optional.of(new Vec3d((double)spawnpoint.getX() + 0.5D, (double)spawnpoint.getY() + 0.1D, (double)spawnpoint.getZ() + 0.5D));
                }
            }

            // Teleport to respawn position
            if (respawnPosition.isPresent()) {

                if ( !isInterdimensional && serverPlayer.getWorld() != targetWorld) {
                    // This mirror is too weak to cross the veil between worlds! Maybe a rare nether metal could help...
                    player.sendMessage(Text.translatable("balancedrecall.fail_cross_dimension"), false);
                    return stack;
                }
                serverPlayer.teleportTo(serverPlayer.getRespawnTarget(false, TeleportTarget.NO_OP));
                targetWorld.playSound(null, spawnpoint, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);

            } else {
                // You have no home bed or charged respawn anchor, or it was obstructed.
                player.sendMessage(Text.translatable("block.minecraft.spawn.not_valid"), false);
                teleportToWorldSpawn(player, serverPlayer);
            }
        } else {
            // You don't have a spawnpoint, teleporting to world spawn instead
            teleportToWorldSpawn(player, serverPlayer);
        }

        // Update statistics
        player.incrementStat(BalancedRecall.RECALLS);
        player.incrementStat(Stats.USED.getOrCreateStat(this));

        // Damage durability
        stack.damage(1, (LivingEntity)player, LivingEntity.getSlotForHand(player.getActiveHand()));

        // Put on cooldown
        player.getItemCooldownManager().set(stack, stack.getItem().getComponents().get(DataComponentTypes.USE_COOLDOWN).getCooldownTicks());

        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 20;
    }

    private void teleportToWorldSpawn(PlayerEntity player, ServerPlayerEntity serverPlayer) {
        if (!isInterdimensional && serverPlayer.getWorld().getRegistryKey() != ServerWorld.OVERWORLD) {
            // This mirror is too weak to cross the veil between worlds! Maybe a rare nether metal could help...
            player.sendMessage(Text.translatable("balancedrecall.fail_cross_dimension"), false);
            return;
        }

        ServerWorld overworld = serverPlayer.getServer().getWorld(ServerWorld.OVERWORLD);
        BlockPos worldSpawn = overworld.getSpawnPos();
        serverPlayer.teleportTo(serverPlayer.getRespawnTarget(false, TeleportTarget.NO_OP));
        overworld.playSound(null, worldSpawn, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);
    }
}