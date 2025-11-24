package dev.royalcore.api.registries;

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
public class CommandHandler {

    @Getter
    private static final CommandHandler commandHandler = new CommandHandler();

    private final List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();

    private CommandHandler() {
    }

    public void register(LiteralCommandNode<CommandSourceStack> commandNode) {
        commandNodes.add(commandNode);
    }

    public void register(LiteralArgumentBuilder<CommandSourceStack> commandBuilder) {
        commandNodes.add(commandBuilder.build());
    }

    public void finish() {
        for (LiteralCommandNode<CommandSourceStack> commandNode : commandNodes) {
            Main.getPlugin().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
                commands.registrar().register(commandNode);
            });
        }
    }

}
