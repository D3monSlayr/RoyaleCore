package dev.royalcore.api.engine;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.royalcore.Main;
import dev.royalcore.api.br.BattleRoyale;
import dev.royalcore.api.item.BattleRoyaleItem;
import dev.royalcore.api.registries.CommandRegistry;
import dev.royalcore.api.registries.FailedBRRegistry;
import dev.royalcore.api.registries.ListenerRegistry;
import dev.royalcore.api.registries.RecipeRegisty;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BattleRoyaleEngine {

    @Getter
    private static final BattleRoyaleEngine battleRoyaleEngine = new BattleRoyaleEngine();

    private BattleRoyaleEngine() {
    }

    public void register(BattleRoyale battleRoyale) {

        List<Listener> listeners = new ArrayList<>();
        List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();
        List<Recipe> recipes = new ArrayList<>();

        // Check for dupes
        for (Scenario scenario : battleRoyale.scenarios()) {

            for (Listener listener : scenario.listenerConsumer().getListeners()) {
                if (!isDuped(listeners, listener, battleRoyale)) {
                    listeners.add(listener);
                }
            }

            for (LiteralCommandNode<CommandSourceStack> commandNode : scenario.commandConsumer().getCommandNodes()) {
                if (!isDuped(commandNodes, commandNode, battleRoyale)) {
                    commandNodes.add(commandNode);
                }
            }

            for (BattleRoyaleItem item : scenario.itemConsumer().getItems()) {

                ShapedRecipe shapedRecipe = item.getShapedRecipe();
                ShapelessRecipe shapelessRecipe = item.getShapelessRecipe();

                if (shapedRecipe != null) {
                    if (!isDuped(recipes, shapedRecipe, battleRoyale)) {
                        recipes.add(shapedRecipe);
                    }
                }

                if (shapelessRecipe != null) {
                    if (!isDuped(recipes, shapelessRecipe, battleRoyale)) {
                        recipes.add(shapelessRecipe);
                    }
                }

            }

            for (Scenario requiredScenario : scenario.requiredScenarios()) {

                if (!battleRoyale.scenarios().contains(requiredScenario)) {
                    FailedBRRegistry.add(battleRoyale);
                    Main.getPlugin().getComponentLogger().error(Component.text("A battle royale doesn't have a required scenario! Failed to load it."), new AlreadyBoundException());
                    return;
                }

            }

            for (Scenario conflictingScenario : scenario.scenarioConflicts()) {

                if (battleRoyale.scenarios().contains(conflictingScenario)) {
                    FailedBRRegistry.add(battleRoyale);
                    Main.getPlugin().getComponentLogger().error(Component.text("A battle royale has been found to have conflicting scenarios! Failed to load it."), new AlreadyBoundException());
                    return;
                }

            }

            for (Map.Entry<List<Duration>, Runnable> entry : scenario.schedulerConsumer().getSchedules().entrySet()) {
                List<Duration> durations = entry.getKey();
                Runnable runnable = entry.getValue();

                if (durations.size() != 2) {
                    FailedBRRegistry.add(battleRoyale);
                    Main.getPlugin().getComponentLogger().error(Component.text("A battle royale with a scenario that does not have the valid amount of values was found. Failed to load!"), new IllegalStateException());
                    return;
                }

                if (durations.get(0) == Duration.ZERO && durations.get(1) == Duration.ZERO) {
                    battleRoyale.onStart(_ -> Bukkit.getScheduler().runTask(Main.getPlugin(), runnable));
                }

                if (durations.get(1) == Duration.ZERO) {
                    battleRoyale.onStart(_ -> Bukkit.getScheduler().runTaskLater(Main.getPlugin(), runnable, durations.getFirst().toMillis()));
                }

                if (durations.get(0) != Duration.ZERO && durations.get(1) != Duration.ZERO) {
                    battleRoyale.onStart(_ -> Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), runnable, durations.getFirst().toMillis(), durations.get(1).toMillis()));
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

        // Register all
        for (Listener listener : listeners) {
            ListenerRegistry.register(listener);
        }

        for (LiteralCommandNode<CommandSourceStack> commandNode : commandNodes) {
            CommandRegistry.register(commandNode);
        }

        for (Recipe recipe : recipes) {
            RecipeRegisty.register(recipe);
        }

    }

    public boolean isDuped(List<Listener> listeners, Listener listener, BattleRoyale battleRoyale) {
        if (listeners.contains(listener)) {
            FailedBRRegistry.add(battleRoyale);
            Main.getPlugin().getComponentLogger().error(Component.text("A battle royale contained a duplicate listener! Failed to load it."), new AlreadyBoundException());
            return true;
        }

        return false;
    }

    public boolean isDuped(List<LiteralCommandNode<CommandSourceStack>> commandNodes, LiteralCommandNode<CommandSourceStack> commandNode, BattleRoyale battleRoyale) {
        if (commandNodes.contains(commandNode)) {
            FailedBRRegistry.add(battleRoyale);
            Main.getPlugin().getComponentLogger().error(Component.text("A battle royale contained a duplicate command! Failed to load it."), new AlreadyBoundException());
            return true;
        }
        return false;
    }

    public boolean isDuped(List<Recipe> recipes, Recipe recipe, BattleRoyale battleRoyale) {
        if (recipe == null) return false;

        if (recipes.contains(recipe)) {
            FailedBRRegistry.add(battleRoyale);
            Main.getPlugin().getComponentLogger().error(Component.text("A battle royale contained a duplicate recipe! Failed to load it."), new AlreadyBoundException());
            return true;
        }

        return false;

    }

}
