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

@UnstableOnServerStart
public class CommandConsumer {

    @Getter
    private final List<LiteralCommandNode<CommandSourceStack>> commandNodes = new ArrayList<>();

    public void register(LiteralCommandNode<CommandSourceStack> commandNode) {

        if (commandNodes.contains(commandNode)) {
            Main.getPlugin().getComponentLogger().error(Component.text("The command is already included in the registry!"), new AlreadyBoundException());
            return;
        }

        commandNodes.add(commandNode);
    }

    public void register(LiteralArgumentBuilder<CommandSourceStack> commandNode) {

        if (commandNodes.contains(commandNode.build())) {
            Main.getPlugin().getComponentLogger().error(Component.text("The command is already included in the registry!"), new AlreadyBoundException());
            return;
        }

        commandNodes.add(commandNode.build());
    }

}
