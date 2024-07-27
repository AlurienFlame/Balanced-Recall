package net.balancedrecall;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class MirrorRecallingModelPredicateProvider implements ClampedModelPredicateProvider {
    public float unclampedCall(ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int seed) {
        if (livingEntity == null || livingEntity.getActiveItem() != itemStack) {
            return 0.0F;
        }
        return (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F;
    }
}
