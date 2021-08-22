package net.balancedrecall;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

public class MirrorRepairingRecipe extends SmithingRecipe {
    final Ingredient base;
	final Ingredient addition;
	final ItemStack result;
    
    public MirrorRepairingRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, base, addition, result);
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

    public boolean matches(Inventory inventory, World world) {
        return super.matches(inventory, world);
	}

    @Override
    public ItemStack craft(Inventory inventory) {
		ItemStack output = super.craft(inventory);

        // Output should have full durability
        output.setDamage(0);

        return output;
	}

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    public static class Serializer implements RecipeSerializer<MirrorRepairingRecipe> {
		public MirrorRepairingRecipe read(Identifier identifier, JsonObject jsonObject) {
			Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
			Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
			ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
			return new MirrorRepairingRecipe(identifier, ingredient, ingredient2, itemStack);
		}

		public MirrorRepairingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			return new MirrorRepairingRecipe(identifier, ingredient, ingredient2, itemStack);
		}

		public void write(PacketByteBuf packetByteBuf, MirrorRepairingRecipe mirrorRepairingRecipe) {
			mirrorRepairingRecipe.base.write(packetByteBuf);
			mirrorRepairingRecipe.addition.write(packetByteBuf);
			packetByteBuf.writeItemStack(mirrorRepairingRecipe.result);
		}
	}
}
