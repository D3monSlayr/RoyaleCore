package dev.royalcore.api.registries;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.royalcore.Main;
import dev.royalcore.annotations.NotForDeveloperUse;
import dev.royalcore.annotations.UnstableOnServerStart;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for deferred registration of Brigadier commands.
 * <p>
 * Command nodes are collected and later registered using Paper's
 * {@link LifecycleEvents#COMMANDS} lifecycle event.
 */
@UnstableOnServerStart
public class CommandRegistry {

    /**
     * Singleton instance of the {@link CommandRegistry}.
     *
     */
    @Getter
    private static final CommandRegistry commandRegistry = new CommandRegistry();

    private final List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();

    private CommandRegistry() {
    }

    /**
     * Adds a Brigadier command node to the internal registry to be registered later.
     *
     * @param commandNode the command node to register
     */
    @UnstableOnServerStart
    public void register(LiteralCommandNode<CommandSourceStack> commandNode) {
        commandNodes.add(commandNode);
    }

    /**
     * Registers all collected command nodes using Paper's command lifecycle event.
     * <p>
     * This should be called once during startup to hook commands into the
     * server's Brigadier command system.
     */
    @NotForDeveloperUse
    @UnstableOnServerStart
    public void finish() {
        for (LiteralCommandNode<CommandSourceStack> commandNode : commandNodes) {
            Main.getPlugin().getLifecycleManager().registerEventHandler(
                    LifecycleEvents.COMMANDS,
                    commands -> commands.registrar().register(commandNode)
            );
        }
    }

}
