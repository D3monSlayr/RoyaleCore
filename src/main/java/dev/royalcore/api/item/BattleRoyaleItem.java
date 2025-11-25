package dev.royalcore.api.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public abstract class BattleRoyaleItem {

    public abstract ItemStack getItem();

    public abstract Recipe getRecipe();

    public abstract NamespacedKey getRecipeKey();

}
