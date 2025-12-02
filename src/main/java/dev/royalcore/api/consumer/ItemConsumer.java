package dev.royalcore.api.consumer;

import dev.royalcore.Main;
import dev.royalcore.api.item.BattleRoyaleItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects {@link BattleRoyaleItem} instances used by a scenario.
 */
public class ItemConsumer {

    @Getter
    private final List<BattleRoyaleItem> items = new ArrayList<>();

    /**
     * Creates a new item consumer.
     */
    public ItemConsumer() {
    }

    /**
     * Adds an item to this consumer if it is not already present and valid.
     *
     * @param item the item to add
     */
    public void add(BattleRoyaleItem item) {

        if (items.contains(item)) {
            Main.getPlugin().getComponentLogger().error(
                    Component.text("An item is already included in the registry!"),
                    new AlreadyBoundException()
            );
            return;
        }

        if (item.getItem() == null) {
            Main.getPlugin().getComponentLogger().error(
                    Component.text("An item is null!"),
                    new IllegalStateException()
            );
            return;
        }

        items.add(item);
    }

}
