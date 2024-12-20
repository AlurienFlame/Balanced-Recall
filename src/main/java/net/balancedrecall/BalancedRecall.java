package net.balancedrecall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class BalancedRecall implements ModInitializer {
    public static final String MODID = "balancedrecall";

    // Items
    // TODO: Experiment with useCooldown setting
    private static final RegistryKey<Item> magic_mirror_key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MODID, "magic_mirror"));
    public static final Item MAGIC_MIRROR = new MagicMirror(new Item.Settings().registryKey(magic_mirror_key).maxDamage(256).repairable(Items.ENDER_PEARL));

    private static final RegistryKey<Item> dimensional_mirror_key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MODID, "dimensional_mirror"));
    public static final Item DIMENSIONAL_MIRROR = new DimensionalMirror(new Item.Settings().registryKey(dimensional_mirror_key).maxDamage(512).fireproof().repairable(Items.ENDER_PEARL));

    private static final RegistryKey<Item> sleeping_mat_key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MODID, "sleeping_mat"));
    public static final Item SLEEPING_MAT = new SleepingMat(new Item.Settings().registryKey(sleeping_mat_key).maxDamage(128));

    // Stats
    public static final Identifier RECALLS = Identifier.of(MODID, "recalls");
    public static final Identifier MAT_SLEEPS = Identifier.of(MODID, "mat_sleeps");

    @Override
    public void onInitialize() {
        // Items
        Registry.register(Registries.ITEM, magic_mirror_key, MAGIC_MIRROR);
        Registry.register(Registries.ITEM, dimensional_mirror_key, DIMENSIONAL_MIRROR);
        Registry.register(Registries.ITEM, sleeping_mat_key, SLEEPING_MAT);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.add(MAGIC_MIRROR);
            content.add(DIMENSIONAL_MIRROR);
            content.add(SLEEPING_MAT);
        });

        // Stats
        Registry.register(Registries.CUSTOM_STAT, "recalls", RECALLS);
        Registry.register(Registries.CUSTOM_STAT, "mat_sleeps", MAT_SLEEPS);

        Stats.CUSTOM.getOrCreateStat(RECALLS, StatFormatter.DEFAULT);
        Stats.CUSTOM.getOrCreateStat(MAT_SLEEPS, StatFormatter.DEFAULT);
    }
}