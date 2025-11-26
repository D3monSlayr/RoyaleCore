package dev.royalcore.api.registries;

import dev.royalcore.Main;
import org.bukkit.inventory.Recipe;

public class RecipeRegisty {

    private RecipeRegisty() {
    }

    public static void register(Recipe recipe) {
        Main.getPlugin().getServer().addRecipe(recipe);
    }

}
