package dev.royalcore.api.registrar;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.royalcore.Main;
import dev.royalcore.annotation.UnusableOnServerStart;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@UnusableOnServerStart
public class CommandRegistrar {

    @Getter
    private static final CommandRegistrar commandRegistrar = new CommandRegistrar();

    private final List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();

    private CommandRegistrar() {
    }

    public final void add(LiteralCommandNode<CommandSourceStack> commandNode) {
        commandNodes.add(commandNode);
    }

    public final void add(LiteralArgumentBuilder<CommandSourceStack> commandNode) {
        commandNodes.add(commandNode.build());
    }

    public final void finish() {
        for (LiteralCommandNode<CommandSourceStack> commandNode : commandNodes) {
            Main.getPlugin().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
                commands.registrar().register(commandNode);
            });
        }
    }

}
