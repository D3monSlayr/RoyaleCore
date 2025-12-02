package dev.royalcore.api.consumer;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Holds configurable messages used by scenarios or battles.
 */
public class MessageConsumer {

    @Getter
    private Component deathMessage = Component.text("ELIMINATION!")
            .color(NamedTextColor.RED)
            .decorate(TextDecoration.BOLD)
            .append(Component.text(" A player has fallen").color(NamedTextColor.WHITE));
    @Getter
    private Component joinMessage = Component.empty();
    @Getter
    private Component leaveMessage = Component.empty();

    /**
     * Creates a new message consumer with default messages.
     */
    public MessageConsumer() {
    }

    /**
     * Sets the death/elimination message.
     *
     * @param deathMessage the message to broadcast on player elimination
     */
    public void deathMessage(Component deathMessage) {
        this.deathMessage = deathMessage;
    }

    /**
     * Sets the join message.
     *
     * @param joinMessage the message to broadcast when a player joins
     */
    public void joinMessage(Component joinMessage) {
        this.joinMessage = joinMessage;
    }

    /**
     * Sets the leave message.
     *
     * @param leaveMessage the message to broadcast when a player leaves
     */
    public void leaveMessage(Component leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

}
