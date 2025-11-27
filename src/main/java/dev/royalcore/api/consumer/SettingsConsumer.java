package dev.royalcore.api.consumer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class SettingsConsumer {

    private final Map<Setting, Object> settings = new HashMap<>();

    public SettingsConsumer() {
        settings.put(Setting.LIFESTEAL, true);
        settings.put(Setting.GRACE, Duration.ofMinutes(20));
        settings.put(Setting.LATE_JOIN_BEHAVIOUR, LateJoinHandling.ALLOW_PARTICIPATION);
    }

    public void lifesteal(boolean bool) {
        settings.replace(Setting.LIFESTEAL, bool);
    }

    public void grace(Duration duration) {
        settings.replace(Setting.GRACE, duration);
    }

    public void lateJoinBehaviour(LateJoinHandling handling) {
        settings.replace(Setting.LATE_JOIN_BEHAVIOUR, handling);
    }

    public Object getSetting(Setting setting) {
        return settings.getOrDefault(setting, null);
    }

    public enum Setting {
        LIFESTEAL,
        GRACE,
        LATE_JOIN_BEHAVIOUR
    }

    public enum LateJoinHandling {
        SPAWN_WITH_SPECTATOR,
        ALLOW_PARTICIPATION,
        ALLOW_BALANCED_PARTICIPATION,
        BAN
    }

}
