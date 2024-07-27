package net.balancedrecall;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class BalancedRecallClient implements ClientModInitializer{

    @Override
    public void onInitializeClient() {
        // Model Predicates
        ModelPredicateProviderRegistry.register(BalancedRecall.MAGIC_MIRROR, Identifier.of("recalling"), new MirrorRecallingModelPredicateProvider());
        ModelPredicateProviderRegistry.register(BalancedRecall.DIMENSIONAL_MIRROR, Identifier.of("recalling"), new MirrorRecallingModelPredicateProvider());
        ModelPredicateProviderRegistry.register(BalancedRecall.MAGIC_MIRROR, Identifier.of("broken"), new MirrorBrokenModelPredicateProvider());
        ModelPredicateProviderRegistry.register(BalancedRecall.DIMENSIONAL_MIRROR, Identifier.of("broken"), new MirrorBrokenModelPredicateProvider());
    }
}
