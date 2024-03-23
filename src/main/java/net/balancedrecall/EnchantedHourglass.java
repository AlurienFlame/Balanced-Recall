package net.balancedrecall;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnchantedHourglass extends Item {
    private BlockPos anchorPos;
    private ServerWorld anchorDim;
    private float anchorYaw;
    private float anchorPitch;
    Timer timer = new Timer(); // FIXME: Shared across all item instances rn
    private static int delayMs = 5000;

    public EnchantedHourglass(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);
        if (anchorPos != null) {
            return TypedActionResult.pass(stack);
        }
        return ItemUsage.consumeHeldItem(world, playerEntity, hand);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // Only run on the server
        if (world.isClient()) {
            return stack;
        }
        ServerWorld serverWorld = (ServerWorld) world;

        // Only players can use this item
        if (!(user instanceof ServerPlayerEntity)) {
            return stack;
        }
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;

        // Save position
        anchorPos = user.getBlockPos();
        anchorDim = serverWorld;
        anchorYaw = user.getYaw();
        anchorPitch = user.getPitch();

        // Start countdown
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                returnToAnchor(serverPlayer);
            }
        };
        timer.schedule(task, delayMs);

        // Play sound
        serverWorld.playSound(null, anchorPos, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS);

        return stack;
    }

    private void returnToAnchor(ServerPlayerEntity serverPlayer) {
        // Teleport the player back to the anchor
        serverPlayer.teleport(anchorDim, anchorPos.getX(), anchorPos.getY(), anchorPos.getZ(), anchorYaw, anchorPitch);
        anchorDim.playSound(null, anchorPos, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS);

        // Reset the anchor
        anchorPos = null;
        anchorDim = null;
        anchorYaw = 0;
        anchorPitch = 0;
    }
}
