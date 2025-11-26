package dev.royalcore.api.engine;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.royalcore.Main;
import dev.royalcore.api.br.BattleRoyale;
import dev.royalcore.api.consumer.SettingsConsumer;
import dev.royalcore.api.item.BattleRoyaleItem;
import dev.royalcore.api.registries.CommandRegistry;
import dev.royalcore.api.registries.FailedBRRegistry;
import dev.royalcore.api.registries.ListenerRegistry;
import dev.royalcore.api.registries.RecipeRegisty;
import dev.royalcore.api.scenario.Scenario;
import dev.royalcore.api.template.Template;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;
import java.util.UUID;

public class BattleRoyaleEngine {

    @Getter
    private static final BattleRoyaleEngine battleRoyaleEngine = new BattleRoyaleEngine();

    private BattleRoyaleEngine() {
    }

    public void register(BattleRoyale battleRoyale) {

        UUID id = battleRoyale.id();

        List<Scenario> scenarios = battleRoyale.scenarios();
        List<Template> templates = battleRoyale.templates();
        SettingsConsumer settings = battleRoyale.settingsConsumer();

        // Scenario registration.
        for (Scenario scenario : scenarios) {

            if (checkRequiredScenarios(scenario, battleRoyale)) {
                registerScenario(scenario);
            } else {
                return;
            }

        }

        // Template registration
        for (Template template : templates) {

            if (checkRequiredScenarios(template, battleRoyale)) {
                registerTemplate(template);
            } else {
                return;
            }

        }

    }

    public boolean checkRequiredScenarios(Scenario scenario, BattleRoyale battleRoyale) {
        for (Scenario scenario1 : scenario.requiredScenarios()) {
            if (!battleRoyale.scenarios().contains(scenario1)) {
                Main.getPlugin().getComponentLogger().error(Component.text("A battle royale with the ID of " + battleRoyale.id() + " does not have the required scenarios for a scenario with the name '" + scenario1.name() + "'"), new IllegalStateException());
                FailedBRRegistry.add(battleRoyale);
                return false;
            }
        }
        return true;
    }

    public boolean checkRequiredScenarios(Template template, BattleRoyale royale) {
        for (Template template1 : template.requiredTemplates()) {
            if (!royale.templates().contains(template1)) {
                Main.getPlugin().getComponentLogger().error(Component.text("A battle royale with the ID of " + royale.id() + " does not have the required templates for a template"), new IllegalStateException());
                FailedBRRegistry.add(royale);
                return false;
            }
        }
        return true;
    }

    public void registerScenario(Scenario scenario) {

        for (Listener listener : scenario.listenerConsumer().getListeners()) {
            ListenerRegistry.register(listener);
        }

        for (LiteralCommandNode<CommandSourceStack> commandNode : scenario.commandConsumer().getCommandNodes()) {
            CommandRegistry.register(commandNode);
        }

        for (BattleRoyaleItem item : scenario.itemConsumer().getItems()) {

            ShapedRecipe shapedRecipe = item.getShapedRecipe();
            ShapelessRecipe shapelessRecipe = item.getShapelessRecipe();

            if (shapedRecipe != null) {
                RecipeRegisty.register(shapedRecipe);
            }
            if (shapelessRecipe != null) {
                RecipeRegisty.register(shapelessRecipe);
            }
        }

    }

    public void registerTemplate(Template template) {
        for (Listener listener : template.listenerConsumer().getListeners()) {
            ListenerRegistry.register(listener);
        }

        for (LiteralCommandNode<CommandSourceStack> commandNode : template.commandConsumer().getCommandNodes()) {
            CommandRegistry.register(commandNode);
        }

        for (BattleRoyaleItem item : template.itemConsumer().getItems()) {

            ShapedRecipe shapedRecipe = item.getShapedRecipe();
            ShapelessRecipe shapelessRecipe = item.getShapelessRecipe();

            if (shapedRecipe != null) {
                RecipeRegisty.register(shapedRecipe);
            }
            if (shapelessRecipe != null) {
                RecipeRegisty.register(shapelessRecipe);
            }
        }
    }

}
