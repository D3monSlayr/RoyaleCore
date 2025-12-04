package dev.royalcore.internal.lifesteal;

import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LifestealListener implements Listener {

    @Setter
    private boolean lifesteal;

    @Setter
    private double maxHearts;

    @Setter
    private TextComponent deathMessage;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!lifesteal) return;
        if (maxHearts < 1) return;

        Player killer = event.getPlayer().getKiller();
        Player dead = event.getPlayer();

        Location location = dead.getLocation();

        dead.getWorld().strikeLightning(location);
        dead.getWorld().dropItem(location, new LifestealHeart(dead.getUniqueId()));

        if (deathMessage != null) event.deathMessage(deathMessage);

    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;

        ItemStack itemStack = event.getItem();
        Player player = event.getPlayer();

        double scale = player.getHealthScale();

        if (scale > maxHearts) return;

        player.setHealthScale(scale + 2.0);

        scale = player.getHealthScale();

        if (player.getHealthScale() > maxHearts) {
            player.setHealthScale(scale - 2.0);
            player.sendRichMessage("<red><bold>You have reached the maximum amount of hearts!");
            return;
        }

        player.getInventory().remove(itemStack);

    }

}
