package dev.royalcore.api.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

/**
 * Base type for custom Battle Royale items with optional crafting recipes.
 */
public abstract class BattleRoyaleItem {

    /**
     * Creates a new battle royale item.
     */
    public BattleRoyaleItem() {
    }

    /**
     * Returns the underlying {@link ItemStack} representing this item.
     *
     * @return the item stack for this custom item
     */
    public abstract ItemStack getItem();

    /**
     * Returns the shaped recipe for this item, if any.
     *
     * @return a {@link ShapedRecipe}, or {@code null} if none is defined
     */
    public ShapedRecipe getShapedRecipe() {
        return null;
    }

    /**
     * Returns the shapeless recipe for this item, if any.
     *
     * @return a {@link ShapelessRecipe}, or {@code null} if none is defined
     */
    public ShapelessRecipe getShapelessRecipe() {
        return null;
    }

    /**
     * Returns the namespaced key for this item's shaped recipe.
     *
     * @return the {@link NamespacedKey} of the shaped recipe
     * @throws NullPointerException if {@link #getShapedRecipe()} returns {@code null}
     */
    public NamespacedKey getShapedRecipeKey() {
        return getShapedRecipe().getKey();
    }

    /**
     * Returns the namespaced key for this item's shapeless recipe.
     *
     * @return the {@link NamespacedKey} of the shapeless recipe
     * @throws NullPointerException if {@link #getShapelessRecipe()} returns {@code null}
     */
    public NamespacedKey getShapelessRecipeKey() {
        return getShapelessRecipe().getKey();
    }

}
