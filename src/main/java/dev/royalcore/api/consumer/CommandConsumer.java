package dev.royalcore.api.consumer;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.royalcore.Main;
import dev.royalcore.annotations.UnstableOnServerStart;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects Brigadier command nodes to be registered for a scenario.
 */
@UnstableOnServerStart
public class CommandConsumer {

    @Getter
    private final List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();

    /**
     * Creates a new command consumer.
     */
    public CommandConsumer() {
    }

    /**
     * Registers a pre-built command node if it is not already present.
     *
     * @param commandNode the command node to register
     */
    public void register(LiteralCommandNode<CommandSourceStack> commandNode) {

        if (commandNodes.contains(commandNode)) {
            Main.getPlugin().getComponentLogger().error(
                    Component.text("The command is already included in the registry!"),
                    new AlreadyBoundException()
            );
            return;
        }

        commandNodes.add(commandNode);
    }

    /**
     * Builds and registers a command node from the given builder if not already present.
     *
     * @param commandNode the builder for the command node
     */
    public void register(LiteralArgumentBuilder<CommandSourceStack> commandNode) {

        if (commandNodes.contains(commandNode.build())) {
            Main.getPlugin().getComponentLogger().error(
                    Component.text("The command is already included in the registry!"),
                    new AlreadyBoundException()
            );
            return;
        }

        commandNodes.add(commandNode.build());
    }

}
