package net.balancedrecall;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class DimensionalMirror extends MagicMirror{
    public DimensionalMirror(net.minecraft.world.item.Item.Properties settings) {
        super(settings);
        isInterdimensional = true;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return (int)(BalancedRecall.config.dimensionalMirrorUseTimeSeconds * 20);
    }
}
