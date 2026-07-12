package net.balancedrecall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class BalancedRecall implements ModInitializer {
    public static final String MODID = "balancedrecall";

    public static final BalancedRecallConfig config = new BalancedRecallConfig();

    // Items
    private static final ResourceKey<Item> magic_mirror_key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, "magic_mirror"));
    public static final Item MAGIC_MIRROR = new MagicMirror(new Item.Properties().setId(magic_mirror_key).durability(256).repairable(Items.ENDER_PEARL).useCooldown(config.getDouble("magic_mirror_cooldown_time_seconds").floatValue()));

    private static final ResourceKey<Item> dimensional_mirror_key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, "dimensional_mirror"));
    public static final Item DIMENSIONAL_MIRROR = new DimensionalMirror(new Item.Properties().setId(dimensional_mirror_key).durability(512).fireResistant().repairable(Items.ENDER_PEARL).useCooldown(config.getDouble("dimensional_mirror_cooldown_time_seconds").floatValue()));

    private static final ResourceKey<Item> sleeping_mat_key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, "sleeping_mat"));
    public static final Item SLEEPING_MAT = new SleepingMat(new Item.Properties().setId(sleeping_mat_key).durability(128));

    // Stats
    public static final Identifier RECALLS = Identifier.fromNamespaceAndPath(MODID, "recalls");
    public static final Identifier MAT_SLEEPS = Identifier.fromNamespaceAndPath(MODID, "mat_sleeps");

    @Override
    public void onInitialize() {
        // Items
        Registry.register(BuiltInRegistries.ITEM, magic_mirror_key, MAGIC_MIRROR);
        Registry.register(BuiltInRegistries.ITEM, dimensional_mirror_key, DIMENSIONAL_MIRROR);
        Registry.register(BuiltInRegistries.ITEM, sleeping_mat_key, SLEEPING_MAT);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.accept(MAGIC_MIRROR);
            content.accept(DIMENSIONAL_MIRROR);
            content.accept(SLEEPING_MAT);
        });

        // Stats
        Registry.register(BuiltInRegistries.CUSTOM_STAT, "recalls", RECALLS);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, "mat_sleeps", MAT_SLEEPS);

        Stats.CUSTOM.get(RECALLS, StatFormatter.DEFAULT);
        Stats.CUSTOM.get(MAT_SLEEPS, StatFormatter.DEFAULT);
    }
}