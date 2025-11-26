package dev.royalcore.api.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public abstract class BattleRoyaleItem {

    public abstract ItemStack getItem();

    public ShapedRecipe getShapedRecipe() {
        return null;
    }

    public ShapelessRecipe getShapelessRecipe() {
        return null;
    }

    public NamespacedKey getShapedRecipeKey() {
        return getShapedRecipe().getKey();
    }

    public NamespacedKey getShapelessRecipeKey() {
        return getShapelessRecipe().getKey();
    }

}
