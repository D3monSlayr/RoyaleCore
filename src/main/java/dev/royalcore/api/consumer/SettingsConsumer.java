package dev.royalcore.api.consumer;

import lombok.Getter;

import java.time.Duration;

public class SettingsConsumer {

    @Getter
    private boolean lifesteal = true;
    @Getter
    private Duration grace = Duration.ZERO;
    @Getter
    private LateJoinHandling lateJoinHandling = LateJoinHandling.ALLOW_PARTICIPATION;

    public void lifesteal(boolean bool) {
        lifesteal = bool;
    }

    public void grace(Duration time) {
        grace = time;
    }

    public void lateJoinBehavior(LateJoinHandling handler) {
        lateJoinHandling = handler;
    }

    public enum LateJoinHandling {
        SPAWN_WITH_SPECTATOR,
        BAN,
        ALLOW_PARTICIPATION,
        ALLOW_BALANCED_PARTICIPATION
    }

}
