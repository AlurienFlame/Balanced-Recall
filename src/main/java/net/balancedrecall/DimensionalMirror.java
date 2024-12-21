package net.balancedrecall;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class DimensionalMirror extends MagicMirror{
    public DimensionalMirror(net.minecraft.item.Item.Settings settings) {
        super(settings);
        isInterdimensional = true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return (int)(BalancedRecall.config.getDouble("dimensional_mirror_use_time_seconds") * 20);
    }
}
