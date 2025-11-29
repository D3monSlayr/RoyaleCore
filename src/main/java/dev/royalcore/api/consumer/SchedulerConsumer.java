package dev.royalcore.api.consumer;

import dev.royalcore.Main;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.rmi.AlreadyBoundException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Collects scheduled tasks associated with a scenario or battle.
 */
public class SchedulerConsumer {

    /**
     * Creates a new scheduler consumer.
     */
    public SchedulerConsumer() {
    }

    @Getter
    private final Map<ScheduleWindow, Runnable> schedules = new HashMap<>();

    /**
     * Registers a runnable to be executed within the given schedule window.
     *
     * @param window   the schedule window describing start and stop offsets
     * @param runnable the runnable to execute
     */
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

    /**
     * Schedules a task to run every tick between the given start and stop offsets.
     *
     * @param start    the start offset from some reference time
     * @param stop     the stop offset from the same reference time
     * @param runnable the runnable to execute repeatedly
     */
    public void scheduleTaskEveryTick(Duration start, Duration stop, Runnable runnable) {
        register(new ScheduleWindow(Optional.of(start), Optional.of(stop)), runnable);
    }

    /**
     * Schedules a one-time task to be executed after the given delay.
     *
     * @param start    the delay before execution
     * @param runnable the runnable to execute
     */
    public void scheduleTaskLater(Duration start, Runnable runnable) {
        register(new ScheduleWindow(Optional.of(start), Optional.empty()), runnable);
    }

    /**
     * Registers a task to be executed immediately or at an engine-defined time.
     *
     * @param runnable the runnable to execute
     */
    public void run(Runnable runnable) {
        register(new ScheduleWindow(Optional.empty(), Optional.empty()), runnable);
    }

    /**
     * Describes a scheduling window using optional start and stop offsets.
     *
     * @param start optional start offset
     * @param stop  optional stop offset
     */
    public record ScheduleWindow(Optional<Duration> start, Optional<Duration> stop) {
    }

}
