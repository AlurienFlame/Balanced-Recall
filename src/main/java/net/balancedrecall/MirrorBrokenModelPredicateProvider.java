package net.balancedrecall;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class MirrorBrokenModelPredicateProvider implements ClampedModelPredicateProvider {
    public float unclampedCall(ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int seed) {
        return (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) ? 1F : 0F;
    }
}
