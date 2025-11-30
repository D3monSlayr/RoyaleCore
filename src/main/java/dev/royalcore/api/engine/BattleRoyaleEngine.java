package dev.royalcore.api.engine;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.royalcore.Main;
import dev.royalcore.annotations.MarkedForRemoval;
import dev.royalcore.api.br.BattleRoyale;
import dev.royalcore.api.errors.Result;
import dev.royalcore.api.item.BattleRoyaleItem;
import dev.royalcore.api.registries.CommandRegistry;
import dev.royalcore.api.registries.FailedBRRegistry;
import dev.royalcore.api.registries.ListenerRegistry;
import dev.royalcore.api.registries.RecipeRegistry;
import dev.royalcore.api.scenario.Scenario;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Legacy engine responsible for validating and registering BattleRoyale definitions.
 * <p>
 * This engine is marked for removal; use the new engine API instead where available.
 */
@MarkedForRemoval
public class BattleRoyaleEngine {

    /**
     * Singleton instance of the legacy {@link BattleRoyaleEngine}.
     *
     */
    @Getter
    private static final BattleRoyaleEngine battleRoyaleEngine = new BattleRoyaleEngine();

    private BattleRoyaleEngine() {
    }

    /**
     * Validates and registers a {@link BattleRoyale} definition.
     * <p>
     * This method:
     * <ul>
     *     <li>Collects listeners, commands and recipes from all scenarios.</li>
     *     <li>Checks required and conflicting scenarios.</li>
     *     <li>Applies scenario border configuration and per-player behaviour.</li>
     *     <li>Registers listeners, commands and recipes through their registries.</li>
     * </ul>
     *
     * @param battleRoyale the battle royale definition to register
     * @return a {@link Result} indicating success or failure and an explanatory message
     */
    public Result register(BattleRoyale battleRoyale) {

        List<Listener> listeners = new ArrayList<>();
        List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();
        List<Recipe> recipes = new ArrayList<>();

        for (Scenario scenario : battleRoyale.scenarios()) {

            for (Listener listener : scenario.listenerConsumer().getListeners()) {
                Result dupResult = isDuped(listeners, listener, battleRoyale);
                if (dupResult.isErr()) {
                    return dupResult;
                }
                listeners.add(listener);
            }

            for (LiteralCommandNode<CommandSourceStack> commandNode : scenario.commandConsumer().getCommandNodes()) {
                Result dupResult = isDuped(commandNodes, commandNode, battleRoyale);
                if (dupResult.isErr()) {
                    return dupResult;
                }
                commandNodes.add(commandNode);
            }

            for (BattleRoyaleItem item : scenario.itemConsumer().getItems()) {

                ShapedRecipe shapedRecipe = item.getShapedRecipe();
                ShapelessRecipe shapelessRecipe = item.getShapelessRecipe();

                if (shapedRecipe != null) {
                    Result dupResult = isDuped(recipes, shapedRecipe, battleRoyale);
                    if (dupResult.isErr()) {
                        return dupResult;
                    }
                    recipes.add(shapedRecipe);
                }

                if (shapelessRecipe != null) {
                    Result dupResult = isDuped(recipes, shapelessRecipe, battleRoyale);
                    if (dupResult.isErr()) {
                        return dupResult;
                    }
                    recipes.add(shapelessRecipe);
                }

            }

            for (Scenario requiredScenario : scenario.requiredScenarios()) {

                if (!battleRoyale.scenarios().contains(requiredScenario)) {
                    FailedBRRegistry.add(battleRoyale);
                    return Result.Err(
                            Component.text("A battle royale doesn't have a required scenario! Failed to load it."),
                            new AlreadyBoundException(),
                            false
                    );
                }

            }

            for (Scenario conflictingScenario : scenario.scenarioConflicts()) {

                if (battleRoyale.scenarios().contains(conflictingScenario)) {
                    FailedBRRegistry.add(battleRoyale);
                    return Result.Err(
                            Component.text("A battle royale has been found to have conflicting scenarios! Failed to load it."),
                            new AlreadyBoundException(),
                            false
                    );
                }

            }

            for (Map.Entry<World, Consumer<WorldBorder>> entry : scenario.borderConsumer().getBorders().entrySet()) {

                World world = entry.getKey();
                Consumer<WorldBorder> worldBorder = entry.getValue();

                worldBorder.accept(world.getWorldBorder());

            }

            battleRoyale.onStart(_ -> {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    scenario.playerConsumer().getPlayerConsumer().accept(player);
                }

            });

        }

        for (Listener listener : listeners) {
            ListenerRegistry.getListenerRegistry().register(listener);
        }

        for (LiteralCommandNode<CommandSourceStack> commandNode : commandNodes) {
            CommandRegistry.getCommandRegistry().register(commandNode);
        }

        for (Recipe recipe : recipes) {
            RecipeRegistry.getRecipeRegistry().register(recipe);
        }

        return Result.Ok(
                Component.text("Successfully registered a battle royale with the ID of " + battleRoyale.id()),
                true
        );

    }

    /**
     * Checks whether a listener is already present in the given list.
     *
     * @param listeners    the list of already collected listeners
     * @param listener     the listener to check
     * @param battleRoyale the battle royale being registered
     * @return a {@link Result} error if a duplicate is found, otherwise a successful result
     */
    public Result isDuped(List<Listener> listeners, Listener listener, BattleRoyale battleRoyale) {
        if (listeners.contains(listener)) {
            FailedBRRegistry.add(battleRoyale);
            Main.getPlugin().getComponentLogger().error(
                    Component.text("A battle royale contained a duplicate listener! Failed to load it."),
                    new AlreadyBoundException()
            );
            return Result.Err(
                    Component.text("Duplicate listener detected in battle royale."),
                    new AlreadyBoundException(),
                    false
            );
        }

        return Result.Ok(Component.text("Listener is unique."), true);
    }

    /**
     * Checks whether a command node is already present in the given list.
     *
     * @param commandNodes the list of already collected command nodes
     * @param commandNode  the command node to check
     * @param battleRoyale the battle royale being registered
     * @return a {@link Result} error if a duplicate is found, otherwise a successful result
     */
    public Result isDuped(
            List<LiteralCommandNode<CommandSourceStack>> commandNodes,
            LiteralCommandNode<CommandSourceStack> commandNode,
            BattleRoyale battleRoyale
    ) {
        if (commandNodes.contains(commandNode)) {
            FailedBRRegistry.add(battleRoyale);
            Main.getPlugin().getComponentLogger().error(
                    Component.text("A battle royale contained a duplicate command! Failed to load it."),
                    new AlreadyBoundException()
            );
            return Result.Err(
                    Component.text("Duplicate command detected in battle royale."),
                    new AlreadyBoundException(),
                    false
            );
        }
        return Result.Ok(Component.text("Command node is unique."), true);
    }

    /**
     * Checks whether a recipe is already present in the given list.
     *
     * @param recipes      the list of already collected recipes
     * @param recipe       the recipe to check
     * @param battleRoyale the battle royale being registered
     * @return a {@link Result} error if a duplicate is found, otherwise a successful result
     */
    public Result isDuped(List<Recipe> recipes, Recipe recipe, BattleRoyale battleRoyale) {
        if (recipe == null) {
            return Result.Ok(Component.text("Recipe is null; skipping duplicate check."), true);
        }

        if (recipes.contains(recipe)) {
            FailedBRRegistry.add(battleRoyale);
            Main.getPlugin().getComponentLogger().error(
                    Component.text("A battle royale contained a duplicate recipe! Failed to load it."),
                    new AlreadyBoundException()
            );
            return Result.Err(
                    Component.text("Duplicate recipe detected in battle royale."),
                    new AlreadyBoundException(),
                    false
            );
        }

        return Result.Ok(Component.text("Recipe is unique."), true);
    }

}
