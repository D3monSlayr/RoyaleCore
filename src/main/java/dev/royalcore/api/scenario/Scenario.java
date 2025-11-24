package dev.royalcore.api.scenario;

import dev.royalcore.api.registries.CommandHandler;
import dev.royalcore.api.registries.EventHandler;

import java.util.function.Consumer;

public class Scenario {
    private Consumer<EventHandler> eventHandlerConsumer;
    private Consumer<CommandHandler> commandHandlerConsumer;

    private Scenario() {
    }

    public static ScenarioBuilder scenario(String name) {
        return new ScenarioBuilder(name);
    }

    public static class ScenarioBuilder {
        private final String name;

        private Consumer<EventHandler> eventHandlerConsumer;
        private Consumer<CommandHandler> commandHandlerConsumer;

        public ScenarioBuilder(String name) {
            this.name = name;
        }

        public ScenarioBuilder events(Consumer<EventHandler> eventHandlerConsumer) {
            this.eventHandlerConsumer = eventHandlerConsumer;
            return this;
        }

        public ScenarioBuilder commands(Consumer<CommandHandler> commandHandlerConsumer) {
            this.commandHandlerConsumer = commandHandlerConsumer;
            return this;
        }

        public Scenario build() {
            Scenario scenario = new Scenario();
            scenario.eventHandlerConsumer = this.eventHandlerConsumer;

            if (eventHandlerConsumer != null) {
                EventHandler eh = EventHandler.getEventHandler();
                eventHandlerConsumer.accept(eh);
            }

            if (commandHandlerConsumer != null) {
                CommandHandler ch = CommandHandler.getCommandHandler();
                commandHandlerConsumer.accept(ch);
            }

            return scenario;
        }
    }
}
