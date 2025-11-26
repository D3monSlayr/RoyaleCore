package dev.royalcore.api.consumer;

import dev.royalcore.Main;
import dev.royalcore.api.item.BattleRoyaleItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import java.util.List;

public class ItemConsumer {

    @Getter
    private final List<BattleRoyaleItem> items = new ArrayList<>();

    public void add(BattleRoyaleItem item) {

        if (items.contains(item)) {
            Main.getPlugin().getComponentLogger().error(Component.text("An item is already included in the registry!"), new AlreadyBoundException());
            return;
        }

        if (item.getItem() == null) {
            Main.getPlugin().getComponentLogger().error(Component.text("An item is null!"), new IllegalStateException());
            return;
        }

        items.add(item);
    }

}
