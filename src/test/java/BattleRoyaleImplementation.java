import dev.royalcore.api.br.BattleRoyale;
import dev.royalcore.api.scenario.Scenario;

public class BattleRoyaleImplementation {

    BattleRoyale battleRoyale = BattleRoyale.battleroyale()
            .scenarios(scenarios -> {
                scenarios.add(Scenario.scenario("test")
                        .listeners(listeners -> {
                            listeners.add(new PlayerJoinEvent());
                        })
                        .commands(commands -> {
                            commands.add(WelcomeCommand.commandNode());
                        })
                        .onStart(() -> {

                        })
                        .build());
            })
            .build();

}
