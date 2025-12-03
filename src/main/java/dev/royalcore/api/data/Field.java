package dev.royalcore.api.data;

/**
 * Immutable key/value pair representing a configuration entry.
 *
 * @param name  the logical name of the field
 * @param value the stored value (may be any type)
 * @param <T>   the generic type of the value
 */
public record Field<T>(String name, T value) {

    public static <T> Field<T> field(String name, T value) {
        return new Field<>(name, value);
    }

}
