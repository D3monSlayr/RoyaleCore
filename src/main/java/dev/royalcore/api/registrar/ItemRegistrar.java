package dev.royalcore.api.registrar;

import dev.royalcore.Main;
import dev.royalcore.annotation.UnusableOnServerStart;
import dev.royalcore.api.item.BattleRoyaleItem;
import dev.royalcore.internal.ItemRegistrarListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@UnusableOnServerStart
public class ItemRegistrar {

    @Getter
    private static final ItemRegistrar itemRegistrar = new ItemRegistrar();

    @Getter
    private final List<BattleRoyaleItem> items = new ArrayList<>();

    private ItemRegistrar() {
    }

    public final void register(BattleRoyaleItem item) {
        items.add(item);
    }

    public final void finish() {
        ListenerRegistrar.getListenerRegistrar().register(new ItemRegistrarListener());

        for (BattleRoyaleItem item : items) {
            Main.getPlugin().getServer().addRecipe(item.getRecipe());
        }

    }

}
