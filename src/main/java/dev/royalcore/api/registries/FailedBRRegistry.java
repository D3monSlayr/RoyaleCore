package dev.royalcore.api.registries;

import dev.royalcore.Main;
import dev.royalcore.annotations.MarkedForRemoval;
import dev.royalcore.api.br.BattleRoyale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Registry tracking BattleRoyale instances that failed validation or registration.
 */
@MarkedForRemoval
public class FailedBRRegistry {

    private static final List<BattleRoyale> failedBRs = new ArrayList<>();

    /**
     * Creates a new failed battle royale registry.
     */
    public FailedBRRegistry() {
    }

    /**
     * Adds a failed battle royale to the registry.
     *
     * @param battleRoyale the failed battle royale
     */
    public static void add(BattleRoyale battleRoyale) {
        failedBRs.add(battleRoyale);
    }

    /**
     * Declares all failed battle royales, typically by logging them.
     */
    public static void declare() {

        if (failedBRs.isEmpty()) return;

        Main.getPlugin().getComponentLogger().info(Component.text("The following Battle Royales failed to load:").color(NamedTextColor.RED));

        for (BattleRoyale br : failedBRs) {
            UUID id = br.id();
            if (id == null) {
                Main.getPlugin().getComponentLogger().info(Component.text("unknown uuid").color(NamedTextColor.DARK_RED).append(Component.newline()));
            } else {
                Main.getPlugin().getComponentLogger().info(Component.text(id.toString()).append(Component.newline()));
            }

        }
    }

    /**
     * Checks whether the given battle royale is marked as failed.
     *
     * @param battleRoyale the battle royale to check
     * @return {@code true} if the given battle royale previously failed, otherwise {@code false}
     */
    public boolean didFail(BattleRoyale battleRoyale) {
        return failedBRs.contains(battleRoyale);
    }

}
