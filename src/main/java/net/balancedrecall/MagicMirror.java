package net.balancedrecall;

import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagicMirror extends Item {
    boolean isInterdimensional;

    public MagicMirror(Settings settings) {
        super(settings);
        isInterdimensional = false;
    }

    // Can't be used on 1 or less durability
    public static boolean isUsable(ItemStack stack) {
		return stack.getDamage() < stack.getMaxDamage() - 1;
	}


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);
        if (MagicMirror.isUsable(stack)) {
            // Use item
            return ItemUsage.consumeHeldItem(world, playerEntity, hand);
        } else {
            // Item is out of durability, don't use it
            return TypedActionResult.pass(stack);
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
            Block respawnBlock = targetWorld.getBlockState(spawnpoint).getBlock();
            Optional<Vec3d> respawnPosition = Optional.empty();

            if (respawnBlock instanceof RespawnAnchorBlock) {
                respawnPosition = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, targetWorld, spawnpoint);

            } else if (respawnBlock instanceof BedBlock) {
                respawnPosition = BedBlock.findWakeUpPosition(EntityType.PLAYER, targetWorld, spawnpoint, serverPlayer.getSpawnAngle());

            } else if (serverPlayer.isSpawnPointSet()){
                // Spawnpoint set by /spawnpoint command or equivalent
                boolean footBlockClear = respawnBlock.canMobSpawnInside();
			    boolean headBlockClear = targetWorld.getBlockState(spawnpoint.up()).getBlock().canMobSpawnInside();
			    if (footBlockClear && headBlockClear) {
                    respawnPosition = Optional.of(new Vec3d((double)spawnpoint.getX() + 0.5D, (double)spawnpoint.getY() + 0.1D, (double)spawnpoint.getZ() + 0.5D));
                }
            }

            // Teleport to respawn position
            if (respawnPosition.isPresent()) {

                if ( !isInterdimensional && serverPlayer.getServerWorld() != targetWorld) {
                    // This mirror is too weak to cross the veil between worlds! Maybe a rare nether metal could help...
                    player.sendMessage(new TranslatableText("balancedrecall.fail_cross_dimension"), false);
                    return stack;
                }

                Vec3d spawnVec = respawnPosition.get();
                serverPlayer.teleport(targetWorld, spawnVec.getX(), spawnVec.getY(), spawnVec.getZ(), serverPlayer.getSpawnAngle(), 0.5F);
                targetWorld.playSound(null, spawnpoint, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);

            } else {
                // You have no home bed or charged respawn anchor, or it was obstructed.
                player.sendMessage(new TranslatableText("block.minecraft.spawn.not_valid"), false);
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
        stack.damage(1, (LivingEntity)player, (Consumer<LivingEntity>)((p) -> {
            p.sendToolBreakStatus(player.getActiveHand());
        }));

        // Put on cooldown
        player.getItemCooldownManager().set(this, 20);

        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20;
    }

    private void teleportToWorldSpawn(PlayerEntity player, ServerPlayerEntity serverPlayer) {
        if (!isInterdimensional && serverPlayer.getServerWorld().getRegistryKey() != ServerWorld.OVERWORLD) {
            // This mirror is too weak to cross the veil between worlds! Maybe a rare nether metal could help...
            player.sendMessage(new TranslatableText("balancedrecall.fail_cross_dimension"), false);
            return;
        }

        ServerWorld overworld = serverPlayer.getServer().getWorld(ServerWorld.OVERWORLD);
        BlockPos worldSpawn = overworld.getSpawnPos();
        serverPlayer.teleport(overworld, worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ(), serverPlayer.getSpawnAngle(), 0.5F);
        overworld.playSound(null, worldSpawn, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);
    }
}