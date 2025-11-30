package dev.royalcore.api.engine;

import dev.royalcore.Main;
import dev.royalcore.annotations.Experimental;
import dev.royalcore.api.consumer.SchedulerConsumer;
import dev.royalcore.api.errors.Result;
import dev.royalcore.api.scenario.Scenario;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * New engine responsible for managing battle royale sessions and state.
 */
@Experimental
public class NewEngine {

    /**
     * Singleton instance of the engine.
     */
    @Getter
    private static final NewEngine engine = new NewEngine();

    /**
     * Creates a new {@link NewEngine} instance.
     * <p>
     * Private to enforce the singleton pattern; use {@link #getEngine()} to access
     * the shared instance.
     */
    private NewEngine() {
    }

    /**
     * Validates all schedules registered for the given {@link Scenario} and binds them
     * to the Bukkit scheduler to be executed when the scenario starts.
     * <p>
     * Each registered {@link SchedulerConsumer.ScheduleWindow} is interpreted as:
     * <ul>
     *     <li>Empty start and stop: run immediately on scenario start.</li>
     *     <li>Present start, empty stop: run once after the given delay.</li>
     *     <li>Present start and stop: run repeatedly with the given initial delay and period.</li>
     * </ul>
     * Any other combination is treated as invalid.
     *
     * @param scenario the scenario whose registered schedules are validated and scheduled
     * @return a {@link Result.Ok} if all schedules are valid and bound,
     * or a {@link Result.Err} if an invalid schedule window is encountered
     */
    public Result validateScheduleForScenario(Scenario scenario) {

        SchedulerConsumer schedulerConsumer = scenario.schedulerConsumer();

        for (Map.Entry<SchedulerConsumer.ScheduleWindow, Runnable> entry : schedulerConsumer.getSchedules().entrySet()) {
            SchedulerConsumer.ScheduleWindow window = entry.getKey();
            Runnable runnable = entry.getValue();

            if (window.start().isEmpty() && window.stop().isEmpty()) {
                scenario.onStart(_ -> Bukkit.getScheduler().runTask(Main.getPlugin(), runnable));
                continue;
            }

            if (window.start().isPresent() && window.stop().isEmpty()) {
                long delayTicks = toTicks(window.start().get());
                scenario.onStart(_ -> Bukkit.getScheduler().runTaskLater(Main.getPlugin(), runnable, delayTicks));
                continue;
            }

            if (window.start().isPresent() && window.stop().isPresent()) {
                long delayTicks = toTicks(window.start().get());
                long periodTicks = toTicks(window.stop().get());
                scenario.onStart(_ -> Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), runnable, delayTicks, periodTicks));
                continue;
            }

            if (window.start().equals(Optional.empty()) && window.start().equals(Optional.of(Duration.ofDays(Long.MAX_VALUE)))) {
                scenario.onStart(_ -> Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), runnable, 0L, 1L));
                continue;
            }

            return Result.Err(
                    Component.text("Failed to schedule a task for Scenario '" + scenario.name() + "' because it contained an invalid value!"),
                    new IllegalStateException("Invalid schedule window in Scenario '" + scenario.name() + "'."),
                    false
            );
        }

        return Result.Ok(Component.text("Successfully validated all schedules"), true);
    }

    /**
     * Converts a {@link Duration} to Minecraft server ticks.
     * <p>
     * Assumes a default rate of 20 ticks per second.
     *
     * @param duration the duration to convert
     * @return the duration expressed in server ticks
     */
    private long toTicks(Duration duration) {
        return duration.toMillis() / 50L;
    }

    /**
     * Checks a list for duplicate elements using their {@link Object#equals(Object)} implementation.
     * <p>
     * When at least one duplicate is present, the provided {@code action} is invoked with
     * the original list (so the caller can inspect or handle duplicates as desired).
     *
     * @param objects the list to inspect for duplicates
     * @param action  the action to execute when duplicates are detected, receiving the original list
     * @param <T>     the element type contained in the list being checked
     * @return a {@link Result.Ok} if no duplicates are found, or a {@link Result.Err}
     * if at least one duplicate exists
     */
    public <T> Result checkDupe(List<T> objects, Consumer<List<T>> action) {
        long distinct = objects.stream().distinct().count();

        if (distinct != objects.size()) {
            action.accept(objects);
            return Result.Err(Component.text("Duplicates detected."), true);
        }

        return Result.Ok(Component.text("No duplicates found."), true);
    }

    /**
     * Checks for commands that share the same first literal (name) and reports duplicates.
     * <p>
     * The first literal is extracted for each command via {@code firstLiteralExtractor}, and
     * any literal value that appears more than once is treated as a duplicate. All commands
     * that participate in such duplicates are passed to {@code onDupes}.
     *
     * @param commands              the list of command objects to inspect
     * @param firstLiteralExtractor a function that extracts the first literal (for example, the
     *                              primary label) from a command instance
     * @param onDupes               a consumer invoked with the list of commands that have duplicate literals
     * @param <C>                   the command type contained in the list
     * @return a {@link Result.Ok} if no duplicate literals are found, or a {@link Result.Err}
     * if any duplicate first literals are detected
     */
    public <C> Result checkCommandLiteralDupes(List<C> commands,
                                               Function<C, String> firstLiteralExtractor,
                                               Consumer<List<C>> onDupes) {
        Map<String, List<C>> byLiteral = commands.stream()
                .collect(Collectors.groupingBy(firstLiteralExtractor));

        List<C> dupes = byLiteral.values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(List::stream)
                .toList();

        if (!dupes.isEmpty()) {
            onDupes.accept(dupes);
            return Result.Err(Component.text("Duplicate command literals detected."), true);
        }

        return Result.Ok(Component.text("No duplicate command literals found."), true);
    }

    /**
     * Executes an action once for each distinct duplicate element in the given list.
     * <p>
     * Elements are compared using {@link Object#equals(Object)}. For every value that appears
     * more than once, {@code action} is invoked exactly once with that value.
     *
     * @param objects the list to inspect for duplicate values
     * @param action  the action to run for each distinct duplicate element found
     * @param <T>     the element type contained in the list
     * @return a {@link Result.Ok} if no duplicates are found, or a {@link Result.Err}
     * describing the duplicates that were detected
     */
    public <T> Result forEachDupe(List<T> objects, Consumer<T> action) {
        var seen = new java.util.HashSet<T>();
        var dupes = new java.util.HashSet<T>();

        for (T obj : objects) {
            if (!seen.add(obj)) {
                dupes.add(obj);
            }
        }

        if (!dupes.isEmpty()) {
            dupes.forEach(action);
            return Result.Err(Component.text("Duplicates detected: " + dupes), true);
        }

        return Result.Ok(Component.text("No duplicates found."), true);
    }

}
