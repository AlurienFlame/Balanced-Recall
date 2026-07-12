package net.balancedrecall;

import net.minecraft.core.BlockPos;

// This gives SleepingMat something to call sleepOnMat from because you can't directly reference a mixin

public interface MatSleepingPlayer {
    void sleepOnMat(BlockPos pos);
}
