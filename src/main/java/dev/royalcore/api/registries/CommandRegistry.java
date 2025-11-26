package dev.royalcore.api.registries;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.royalcore.Main;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class CommandRegistry {

    private CommandRegistry() {
    }

    public static void register(LiteralCommandNode<CommandSourceStack> commandNode) {
        Main.getPlugin().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(commandNode);
        });
    }

}
