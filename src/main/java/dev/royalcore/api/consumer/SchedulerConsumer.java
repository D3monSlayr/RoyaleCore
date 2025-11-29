package dev.royalcore.api.consumer;

import dev.royalcore.Main;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.rmi.AlreadyBoundException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SchedulerConsumer {

    @Getter
    private final Map<ScheduleWindow, Runnable> schedules = new HashMap<>();

    private void register(ScheduleWindow window, Runnable runnable) {
        if (schedules.containsValue(runnable)) {
            Main.getPlugin().getComponentLogger().error(
                    Component.text("A runnable is already included in the registry!"),
                    new AlreadyBoundException()
            );
            return;
        }
        schedules.put(window, runnable);
    }

    public void scheduleTaskEveryTick(Duration start, Duration stop, Runnable runnable) {
        register(new ScheduleWindow(Optional.of(start), Optional.of(stop)), runnable);
    }

    public void scheduleTaskLater(Duration start, Runnable runnable) {
        register(new ScheduleWindow(Optional.of(start), Optional.empty()), runnable);
    }

    public void run(Runnable runnable) {
        register(new ScheduleWindow(Optional.empty(), Optional.empty()), runnable);
    }

    public record ScheduleWindow(Optional<Duration> start, Optional<Duration> stop) {
    }

}
