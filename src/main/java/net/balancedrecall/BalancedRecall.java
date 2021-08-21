package net.balancedrecall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BalancedRecall implements ModInitializer {

    public static final Item MAGIC_MIRROR = new MagicMirror(new FabricItemSettings().group(ItemGroup.TOOLS).maxDamage(256));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("balancedrecall", "magic_mirror"), MAGIC_MIRROR);
    }
}