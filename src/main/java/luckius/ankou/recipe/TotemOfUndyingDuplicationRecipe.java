package luckius.ankou.recipe;

import luckius.ankou.AnkuUtils;
import luckius.ankou.Mod;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class TotemOfUndyingDuplicationRecipe extends SpecialCraftingRecipe {
    public TotemOfUndyingDuplicationRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        boolean enderEye = false;
        int totemCount = 0;
        boolean linkedTotem = false;
        boolean normalTotem = false;

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isEmpty()) continue;

            if (itemStack.isOf(Items.TOTEM_OF_UNDYING)) {
                if (AnkuUtils.hasLodestone(itemStack)) {
                    if (!linkedTotem) {
                        linkedTotem = true;
                    }
                    totemCount += 1;
                } else {
                    if (!normalTotem) {
                        normalTotem = true;
                    }
                    totemCount += 1;
                }
                continue;
            }

            if (itemStack.isOf(Items.ENDER_EYE) && !enderEye) {
                enderEye = true;
            }
        }

        return enderEye && linkedTotem && normalTotem && totemCount == 2 && Mod.CONFIG.isLinkable();
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack itemStack = new ItemStack(Items.TOTEM_OF_UNDYING, 1);
        NbtCompound nbt = itemStack.getOrCreateNbt();

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack2 = inventory.getStack(i);
            if (itemStack2.isOf(Items.TOTEM_OF_UNDYING)) {
                if (AnkuUtils.hasLodestone(itemStack2)) {
                    NbtCompound nbt2 = itemStack2.getNbt();
                    AnkuUtils.writeLodestoneNbt(AnkuUtils.getLodestoneDimension(nbt2).get(), AnkuUtils.getLodestonePosition(nbt2), nbt);
                    break;
                }
            }
        }
        return itemStack;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            Item item = inventory.getStack(i).getItem();

            if (inventory.getStack(i).isOf(Items.TOTEM_OF_UNDYING) && AnkuUtils.hasLodestone(inventory.getStack(i))) {
                ItemStack stack = new ItemStack(item);
                stack.setNbt(inventory.getStack(i).getOrCreateNbt());
                defaultedList.set(i, stack);
            }
        }
        return defaultedList;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
