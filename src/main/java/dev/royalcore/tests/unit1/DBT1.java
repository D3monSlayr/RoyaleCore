package dev.royalcore.tests.unit1;

import dev.royalcore.Main;
import dev.royalcore.annotations.NotForDeveloperUse;
import dev.royalcore.api.db.Database;
import dev.royalcore.api.registries.ListenerRegistry;
import dev.royalcore.tests.TestInterface;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.Listener;

@NotForDeveloperUse
public class DBT1 implements TestInterface {

    @Override
    public void activate() {

        Database db = new Database("tests/RoyaleCore/test.db", Main.getPlugin());

        Database.DatabaseSession session = db.use("unit-1");

        session.ensureExists();

        session.readObject("listener", Listener.class)
                .thenAccept(listener -> {

                    if (listener == null) {
                        session.ensureExists()
                                .thenCompose(v -> session.write("listener", new DBL1()))
                                .exceptionally(ex -> {
                                    Main.getPlugin().getComponentLogger().error(Component.text("Test unit 1 failed!"), ex.getMessage());
                                    return null;
                                });
                        Main.getPlugin().getComponentLogger().error(Component.text("Failed to find Test unit 1's listener. It returned null!").color(NamedTextColor.RED));
                        return;
                    }

                    Main.getPlugin().getComponentLogger().info(Component.text("Test unit 1 has succeeded!").color(NamedTextColor.GREEN));

                    try {
                        ListenerRegistry.register(listener);
                    } catch (Exception e) {
                        Main.getPlugin().getComponentLogger().error(Component.text("Failed to register listener in Test unit 1!"), e.getMessage());
                    }

                });

    }
}
