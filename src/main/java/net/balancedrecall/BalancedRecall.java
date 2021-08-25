package net.balancedrecall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BalancedRecall implements ModInitializer {
    public static final String MODID = "balancedrecall";

    // Items
    public static final Item MAGIC_MIRROR = new MagicMirror(new FabricItemSettings().group(ItemGroup.TOOLS).maxDamage(256));
    public static final Item DIMENSIONAL_MIRROR = new DimensionalMirror(new FabricItemSettings().group(ItemGroup.TOOLS).maxDamage(512));
    public static final Item SLEEPING_MAT = new SleepingMat(new FabricItemSettings().group(ItemGroup.TOOLS).maxDamage(128));

    // Recipe
    public static final RecipeSerializer<MirrorRepairingRecipe> MIRROR_REPAIRING_SERIALIZER = new MirrorRepairingRecipe.Serializer();

    // Stats
    public static final Identifier RECALLS = new Identifier(MODID, "recalls");
    public static final Identifier MAT_SLEEPS = new Identifier(MODID, "mat_sleeps");

    @Override
    public void onInitialize() {
        // Items
        Registry.register(Registry.ITEM, new Identifier(MODID, "magic_mirror"), MAGIC_MIRROR);
        Registry.register(Registry.ITEM, new Identifier(MODID, "dimensional_mirror"), DIMENSIONAL_MIRROR);
        Registry.register(Registry.ITEM, new Identifier(MODID, "sleeping_mat"), SLEEPING_MAT);

        // Recipe
        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "mirror_repairing"), MIRROR_REPAIRING_SERIALIZER);

        // Stats
        Registry.register(Registry.CUSTOM_STAT, "recalls", RECALLS);
        Registry.register(Registry.CUSTOM_STAT, "mat_sleeps", MAT_SLEEPS);

        Stats.CUSTOM.getOrCreateStat(RECALLS, StatFormatter.DEFAULT);
        Stats.CUSTOM.getOrCreateStat(MAT_SLEEPS, StatFormatter.DEFAULT);
    }
}