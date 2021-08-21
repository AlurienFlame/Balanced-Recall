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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagicMirror extends Item {

    boolean interdimensional = false;

    public MagicMirror(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        return ItemUsage.consumeHeldItem(world, playerEntity, hand);
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // finishUsing runs on both the server and the client (for some reason),
        // but we only want to run this code on the server.
        if (world.isClient()) {
            return stack;
        }
        
        // TODO: Set up a durability 0 failure case, similar to broken Elytra

        PlayerEntity player = (PlayerEntity) user;
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;
        ServerWorld targetWorld = serverPlayer.server.getWorld(serverPlayer.getSpawnPointDimension());

        if ( !interdimensional && serverPlayer.getServerWorld() != targetWorld) {
            // This mirror is too weak to cross the veil between worlds! Maybe a rare nether metal could help...
            player.sendSystemMessage(new TranslatableText("balancedrecall.fail_cross_dimension"), Util.NIL_UUID);
            return stack;
        }

        BlockPos spawnpoint = serverPlayer.getSpawnPointPosition();

        if (spawnpoint != null) {
            // Player spawn
            System.out.println("Teleporting to player spawn...");

            // Find respawn position
            // PlayerEntity.findRespawnPosition exhausts respawn anchor charges, which is undesirable, so instead we replicate its functionality directly
            Block respawnBlock = targetWorld.getBlockState(spawnpoint).getBlock();
            Optional<Vec3d> respawnPosition = Optional.empty();

            if (respawnBlock instanceof RespawnAnchorBlock) {
                respawnPosition = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, spawnpoint);

            } else if (respawnBlock instanceof BedBlock) {
                respawnPosition = BedBlock.findWakeUpPosition(EntityType.PLAYER, world, spawnpoint, serverPlayer.getSpawnAngle());

            } else {
                // Spawnpoint isn't attatched to a respawn anchor or bed, find a safe place to spawn directly
                boolean footBlockClear = respawnBlock.canMobSpawnInside();
			    boolean headBlockClear = world.getBlockState(spawnpoint.up()).getBlock().canMobSpawnInside();
			    if (footBlockClear && headBlockClear) {
                    respawnPosition = Optional.of(new Vec3d((double)spawnpoint.getX() + 0.5D, (double)spawnpoint.getY() + 0.1D, (double)spawnpoint.getZ() + 0.5D));
                } else {
                    respawnPosition = Optional.empty(); 
                }
            }

            // Teleport to respawn position
            if (respawnPosition.isPresent()) {
                Vec3d spawnVec = respawnPosition.get();
                serverPlayer.teleport(targetWorld, spawnVec.getX(), spawnVec.getY(), spawnVec.getZ(), serverPlayer.getSpawnAngle(), 0.5F);
            } else {
                // No space, fall back to world spawn
                player.sendSystemMessage(new TranslatableText("balancedrecall.spawn_obstructed"), Util.NIL_UUID);
                teleportToWorldSpawn(player, serverPlayer);
            }
        } else {
            // World spawn
            teleportToWorldSpawn(player, serverPlayer);
        }
        targetWorld.playSound(null, spawnpoint, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);

        // TODO: Instead of breaking, become non-functional on 1 durability like elytra do
        stack.damage(1, (LivingEntity)player, (Consumer<LivingEntity>)((p) -> {
            System.out.println("Used 1 durability on magic mirror");
            p.sendToolBreakStatus(player.getActiveHand());
        }));

        player.getItemCooldownManager().set(this, 20);

        return stack;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 20;
    }

    private void teleportToWorldSpawn(PlayerEntity player, ServerPlayerEntity serverPlayer) {
        if (!interdimensional && serverPlayer.getServerWorld().getRegistryKey() != ServerWorld.OVERWORLD) {
            // This mirror is too weak to cross the veil between worlds! Maybe a rare nether metal could help...
            player.sendSystemMessage(new TranslatableText("balancedrecall.fail_cross_dimension"), Util.NIL_UUID);
            return;
        }

        System.out.println("Teleporting to world spawn...");
        ServerWorld overworld = serverPlayer.getServer().getWorld(ServerWorld.OVERWORLD);
        BlockPos worldSpawn = overworld.getSpawnPos();
        serverPlayer.teleport(overworld, worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ(), serverPlayer.getSpawnAngle(), 0.5F);
    }
}