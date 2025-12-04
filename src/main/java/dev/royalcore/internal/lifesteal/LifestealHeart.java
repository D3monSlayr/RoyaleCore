package dev.royalcore.internal.lifesteal;

import dev.royalcore.internal.Namespaces;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class LifestealHeart extends ItemStack {

    public LifestealHeart(UUID player) {

        super(Material.NETHER_STAR);

        editPersistentDataContainer(container -> {
            container.set(Namespaces.LIFESTEAL_HEART_ITEM_KEY, PersistentDataType.STRING, player.toString());
        });

        setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString("lifesteal_heart")
                .build());

        setData(DataComponentTypes.ITEM_NAME, Component.text("Heart").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD));

        setData(DataComponentTypes.LORE, ItemLore.lore()
                .addLine(Component.text("Right click to withdraw!").color(NamedTextColor.RED))
                .build());

        setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

    }

}
