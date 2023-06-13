package net.balancedrecall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BalancedRecall implements ModInitializer {
    public static final String MODID = "balancedrecall";

    // Items
    public static final Item MAGIC_MIRROR = new MagicMirror(new FabricItemSettings().maxDamage(256));
    public static final Item DIMENSIONAL_MIRROR = new DimensionalMirror(new FabricItemSettings().maxDamage(512).fireproof());
    public static final Item SLEEPING_MAT = new SleepingMat(new FabricItemSettings().maxDamage(128));

    // Stats
    public static final Identifier RECALLS = new Identifier(MODID, "recalls");
    public static final Identifier MAT_SLEEPS = new Identifier(MODID, "mat_sleeps");

    @Override
    public void onInitialize() {
        // Items
        Registry.register(Registries.ITEM, new Identifier(MODID, "magic_mirror"), MAGIC_MIRROR);
        Registry.register(Registries.ITEM, new Identifier(MODID, "dimensional_mirror"), DIMENSIONAL_MIRROR);
        Registry.register(Registries.ITEM, new Identifier(MODID, "sleeping_mat"), SLEEPING_MAT);

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