package net.balancedrecall;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class BalancedRecallClient implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        // Model Predicates
        ModelPredicateProviderRegistry.register(BalancedRecall.MAGIC_MIRROR, new Identifier("recalling"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int seed) -> {
            if (livingEntity == null || livingEntity.getActiveItem() != itemStack) {
                return 0.0F;
            }
            return (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });
        ModelPredicateProviderRegistry.register(BalancedRecall.DIMENSIONAL_MIRROR, new Identifier("recalling"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int seed) -> {
            if (livingEntity == null || livingEntity.getActiveItem() != itemStack) {
                return 0.0F;
            }
            return (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });
        ModelPredicateProviderRegistry.register(BalancedRecall.MAGIC_MIRROR, new Identifier("broken"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int seed) -> {
            return (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) ? 1F : 0F;
        });
        ModelPredicateProviderRegistry.register(BalancedRecall.DIMENSIONAL_MIRROR, new Identifier("broken"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int seed) -> {
            return (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) ? 1F : 0F;
        });
    }
    
}
