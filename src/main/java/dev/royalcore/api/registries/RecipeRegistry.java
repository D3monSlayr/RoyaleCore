package dev.royalcore.api.registries;

import dev.royalcore.Main;
import dev.royalcore.annotations.NotForDeveloperUse;
import dev.royalcore.annotations.UnstableOnServerStart;
import lombok.Getter;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for deferred registration of {@link Recipe} instances.
 * <p>
 * Recipes are collected during setup and applied to the server in a single
 * batch via {@link #finish()}, typically during plugin startup.
 */
@UnstableOnServerStart
public class RecipeRegistry {

    /**
     * Singleton instance of the {@link RecipeRegistry}.
     *
     */
    @Getter
    private static final RecipeRegistry recipeRegistry = new RecipeRegistry();
    private final List<Recipe> recipes = new ArrayList<>();

    private RecipeRegistry() {
    }

    /**
     * Adds a {@link Recipe} to the internal registry to be registered later.
     *
     * @param recipe the recipe to register
     */
    @UnstableOnServerStart
    public void register(Recipe recipe) {
        recipes.add(recipe);
    }

    /**
     * Registers all collected recipes with the server.
     * <p>
     * This should be called once during startup after all recipes
     * have been registered with this registry.
     */
    @UnstableOnServerStart
    @NotForDeveloperUse
    public void finish() {
        for (Recipe recipe : recipes) {
            Main.getPlugin().getServer().addRecipe(recipe);
        }
    }

}
