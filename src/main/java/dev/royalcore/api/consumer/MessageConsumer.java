package dev.royalcore.api.consumer;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class MessageConsumer {

    @Getter
    private Component deathMessage = Component.text("ELIMINATION!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
            .append(Component.text(" A player has fallen").color(NamedTextColor.WHITE));

    @Getter
    private Component joinMessage = Component.empty();

    @Getter
    private Component leaveMessage = Component.empty();

    public void deathMessage(Component deathMessage) {
        this.deathMessage = deathMessage;
    }

    public void joinMessage(Component joinMessage) {
        this.joinMessage = joinMessage;
    }

    public void leaveMessage(Component leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

}
