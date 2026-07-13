package net.balancedrecall;

import org.jspecify.annotations.NonNull;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DimensionalMirror extends MagicMirror{
    public DimensionalMirror(net.minecraft.world.item.Item.Properties settings) {
        super(settings);
        isInterdimensional = true;
    }

    @Override
    public int getUseDuration(@NonNull ItemStack stack, @NonNull LivingEntity user) {
        return (int)(BalancedRecall.config.getDouble("dimensional_mirror_use_time_seconds") * 20);
    }
}
