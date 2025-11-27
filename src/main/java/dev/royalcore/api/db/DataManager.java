package dev.royalcore.api.db;

import dev.royalcore.Main;
import dev.royalcore.api.br.BattleRoyale;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public class DataManager {

    @Getter
    public static final DataManager dataManager = new DataManager();

    private DataManager() {
    }

    public void save(Database database, BattleRoyale royale) {

        Database.DatabaseSession battle = database.use(royale.id().toString());

        battle.ensureExists()
                .thenCompose(v -> battle.write("state", royale.state()))
                .thenCompose(v -> battle.write("on-start", royale.onStart()))
                .thenCompose(v -> battle.write("on-stop", royale.onStop()))
                .exceptionally(ex -> {
                    Main.getPlugin().getComponentLogger().error(Component.text("Failed to save a battle royale to the database!"), ex.getMessage());
                    return null;
                });

    }

}
