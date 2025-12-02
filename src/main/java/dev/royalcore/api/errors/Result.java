package dev.royalcore.api.errors;

import dev.royalcore.Main;
import dev.royalcore.annotations.Experimental;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the outcome of an operation within the RoyaleCore API.
 * <p>
 * A {@code Result} is a sealed interface with three possible implementations:
 * {@link Result.Ok} for successful outcomes, {@link Result.Err} for failures,
 * and {@link Result.Broadcast} for aggregating and printing multiple results.
 */
@Experimental
public sealed interface Result
        permits Result.Ok, Result.Err, Result.Broadcast {

    /**
     * Creates a successful {@link Ok} result with an empty success message,
     * logged unconditionally (not only in debug mode).
     *
     * @return a new {@link Ok} result with an empty message and {@code onlyIfDebugModeIsEnabled = false}
     */
    static Ok Ok() {
        return new Ok(Component.empty(), false);
    }

    /**
     * Creates a successful {@link Ok} result with the given success message.
     *
     * @param msg                      the success message to log
     * @param onlyIfDebugModeIsEnabled whether the message should be logged only when debug mode is enabled
     * @return a new {@link Ok} result carrying the given message and debug flag
     */
    static Ok Ok(TextComponent msg, boolean onlyIfDebugModeIsEnabled) {
        return new Ok(msg, onlyIfDebugModeIsEnabled);
    }

    /**
     * Creates an error {@link Err} result with an empty message and no exception,
     * logged unconditionally (not only in debug mode).
     *
     * @return a new {@link Err} result with an empty message and no exception
     */
    static Err Err() {
        return new Err(Component.empty(), null, false);
    }

    /**
     * Creates an error {@link Err} result with the given exception and an empty message.
     *
     * @param exception the exception associated with this error
     * @return a new {@link Err} result with the given exception and an empty message
     */
    static Err Err(Exception exception) {
        return new Err(Component.empty(), exception, false);
    }

    /**
     * Creates an error {@link Err} result with the given message and optional debug-only logging.
     *
     * @param msg                      the error message to log
     * @param onlyIfDebugModeIsEnabled whether the message should be logged only when debug mode is enabled
     * @return a new {@link Err} result with the given message and debug flag
     */
    static Err Err(TextComponent msg, boolean onlyIfDebugModeIsEnabled) {
        return new Err(msg, null, onlyIfDebugModeIsEnabled);
    }

    /**
     * Creates an error {@link Err} result with the given message and exception.
     *
     * @param msg                      the error message to log
     * @param exception                the exception associated with this error
     * @param onlyIfDebugModeIsEnabled whether the message should be logged only when debug mode is enabled
     * @return a new {@link Err} result with the given message, exception, and debug flag
     */
    static Err Err(TextComponent msg, Exception exception, boolean onlyIfDebugModeIsEnabled) {
        return new Err(msg, exception, onlyIfDebugModeIsEnabled);
    }

    /**
     * Creates a new {@link Broadcast} result container to collect multiple {@link Result} instances.
     *
     * @return a new empty {@link Broadcast} result aggregator
     */
    static Broadcast Broadcast() {
        return new Broadcast();
    }

    /**
     * Checks whether the given {@link Result} instance represents a successful outcome.
     *
     * @param r the result to inspect
     * @return {@code true} if the result is an instance of {@link Ok}, {@code false} otherwise
     */
    static boolean isOk(Result r) {
        return r instanceof Result.Ok;
    }

    /**
     * Checks whether the given {@link Result} instance represents an error outcome.
     *
     * @param r the result to inspect
     * @return {@code true} if the result is an instance of {@link Err}, {@code false} otherwise
     */
    static boolean isErr(Result r) {
        return r instanceof Result.Err;
    }

    /**
     * Returns whether this result represents an error outcome.
     *
     * @return {@code true} if this result is an {@link Result.Err}, otherwise {@code false}
     */
    default boolean isErr() {
        return this instanceof Result.Err;
    }

    /**
     * Returns whether this result represents a successful outcome.
     *
     * @return {@code true} if this result is an {@link Result.Ok}, otherwise {@code false}
     */
    default boolean isOk() {
        return this instanceof Result.Ok;
    }

    /**
     * Prints this result to the configured log outputs.
     * <p>
     * The exact logging behavior depends on the concrete implementation:
     * {@link Ok} logs a success message, {@link Err} logs an error message (and optional exception),
     * and {@link Broadcast} prints all contained results.
     */
    void print();

    /**
     * Successful {@link Result} carrying an optional success message and a debug-only flag.
     *
     * @param successMsg               the success message associated with this result
     * @param onlyIfDebugModeIsEnabled whether the message should be logged only when debug mode is enabled
     */
    record Ok(TextComponent successMsg, boolean onlyIfDebugModeIsEnabled) implements Result {
        /**
         * Logs the success message either always or only when debug mode is enabled,
         * depending on {@code onlyIfDebugModeIsEnabled}.
         */
        @Override
        public void print() {
            if (onlyIfDebugModeIsEnabled) {
                Main.debug(successMsg);
                return;
            }

            Main.getPlugin().getComponentLogger().info(successMsg);
        }
    }

    /**
     * Error {@link Result} carrying an error message, an optional exception, and a debug-only flag.
     *
     * @param errorMsg             the error message associated with this result
     * @param exception            the exception linked to this error, or {@code null} if none
     * @param onlyIfDebugIsEnabled whether the error should be logged only when debug mode is enabled
     */
    record Err(TextComponent errorMsg, Exception exception, boolean onlyIfDebugIsEnabled) implements Result {

        /**
         * Logs the error message (and associated exception, if present).
         * <p>
         * If {@code onlyIfDebugIsEnabled} is {@code true}, the error is logged via the debug methods on {@link Main}.
         * Otherwise, it is logged to the plugin's error logger.
         */
        @Override
        public void print() {
            if (onlyIfDebugIsEnabled) {
                if (exception == null) {
                    Main.debug(errorMsg);
                } else {
                    Main.debug(errorMsg, exception);
                }
                return;
            }

            if (exception == null) {
                Main.getPlugin().getComponentLogger().error(errorMsg);
            } else {
                Main.getPlugin().getComponentLogger().error(errorMsg, exception);
            }
        }
    }

    /**
     * Aggregates multiple {@link Result} instances and prints them together.
     * <p>
     * This is useful when an operation consists of several sub-steps, each of which returns a {@link Result},
     * and you want to collect and output all of them at once.
     */
    final class Broadcast implements Result {
        private final List<Result> results = new ArrayList<>();

        /**
         * Creates an empty broadcast result collection.
         */
        public Broadcast() {
        }

        /**
         * Adds a result to this broadcast collection.
         *
         * @param result the result to add
         * @return this {@link Broadcast} instance for chaining
         */
        public Broadcast add(Result result) {
            results.add(result);
            return this;
        }

        /**
         * Removes a result from this broadcast collection.
         *
         * @param result the result to remove
         * @return this {@link Broadcast} instance for chaining
         */
        public Broadcast remove(Result result) {
            results.remove(result);
            return this;
        }

        /**
         * Prints all contained results by invoking {@link Result#print()} on each one in insertion order.
         */
        @Override
        public void print() {
            for (Result r : results) {
                r.print();
            }
        }
    }
}
