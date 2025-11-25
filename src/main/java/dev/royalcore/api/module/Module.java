package dev.royalcore.api.module;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.royalcore.annotation.SubjectToChange;
import dev.royalcore.api.item.BattleRoyaleItem;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public record Module(UUID id, List<Listener> listeners, List<BattleRoyaleItem> items,
                     List<LiteralCommandNode<CommandSourceStack>> commands) {

    public static ModuleBuilder module(UUID id) {
        return new ModuleBuilder(id);
    }

    public static class ModuleBuilder {
        private final UUID id;

        private final List<Listener> listeners = new ArrayList<>();
        private final List<BattleRoyaleItem> items = new ArrayList<>();
        private final List<LiteralCommandNode<CommandSourceStack>> commands = new ArrayList<>();

        public ModuleBuilder(UUID id) {
            this.id = id;
        }

        @SubjectToChange
        public ModuleBuilder listeners(Consumer<List<Listener>> listenerHandler) {
            listenerHandler.accept(listeners);
            return this;
        }

        @SubjectToChange
        public ModuleBuilder items(Consumer<List<BattleRoyaleItem>> itemHandler) {
            itemHandler.accept(items);
            return this;
        }

        @SubjectToChange
        public ModuleBuilder commands(Consumer<List<LiteralCommandNode<CommandSourceStack>>> commandsHandler) {
            commandsHandler.accept(commands);
            return this;
        }

        public Module build() {
            if (id == null) {
                throw new IllegalStateException("Module ID must be set");
            }
            return new Module(id, listeners, items, commands);
        }

    }

}
