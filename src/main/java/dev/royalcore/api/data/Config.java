package dev.royalcore.api.data;

import dev.royalcore.annotations.Experimental;
import dev.royalcore.api.errors.Result;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Simple in-memory configuration model backed by a single YAML file.
 * <p>
 * This class tracks a list of named {@link Field} entries and provides:
 * <ul>
 *     <li>Methods to add fields programmatically.</li>
 *     <li>Lookup utilities to retrieve fields or their values by name.</li>
 *     <li>A {@link #save()} method to serialize all fields to disk in YAML format.</li>
 * </ul>
 * The class does not currently support loading/parsing from disk and
 * assumes that all fields are defined in code.
 */
@Experimental
public class Config {

    private final File file;
    private final List<Field<?>> fields = new ArrayList<>();

    /**
     * Creates a new configuration bound to a file with the given name in the
     * provided directory path.
     * <p>
     * If the file does not exist, it is created immediately. Uses .yml extension by default.
     *
     * @param name the file name (e.g. {@code "config.yml"})
     * @param path the directory path in which the file will reside
     */
    public Config(String name, Path path) {
        if (!name.endsWith(".yml")) {
            name += ".yml";
        }
        file = new File(path.toFile(), name);

        try {
            if (!file.exists()) {
                boolean cf = file.createNewFile();

                if (!cf) {
                    Result.Err(Component.text("Failed to create '" + name + "'"), false);
                }
            }
        } catch (IOException e) {
            Result.Err(Component.text("Failed to create '" + name + "'"), new RuntimeException(e), false);
        }
    }

    /**
     * Adds a field to this configuration.
     * <p>
     * If another field with the same name already exists, it is not removed
     * or replaced; it is the caller's responsibility to avoid duplicates
     * or handle them when reading.
     *
     * @param field the field to add
     */
    public void addField(Field<?> field) {
        fields.add(field);
    }

    /**
     * Updates the value of the given field instance.
     *
     * @param field    The existing field instance to update
     * @param newValue The new value to set
     * @return true if updated successfully, false if field not found
     */
    public boolean setFieldValue(Field<?> field, Object newValue) {
        if (field == null) return false;

        int index = fields.indexOf(field);
        if (index == -1) return false;

        Field<Object> newField = new Field<>(field.name(), newValue);
        fields.set(index, newField);
        return true;
    }

    /**
     * Renames the given field instance and optionally updates its value.
     *
     * @param field    The existing field instance to rename
     * @param newName  The new name for the field
     * @param newValue Optional new value; null to keep existing
     * @return Result Ok() if renamed, Err() if field not found
     */
    public Result renameField(Field<?> field, String newName, Object newValue) {
        if (field == null) return Result.Err(Component.text("Field is null"), false);

        int index = fields.indexOf(field);
        if (index == -1) {
            return Result.Err(Component.text("Field '" + field.name() + "' not found"), false);
        }

        Object valueToUse = newValue != null ? newValue : field.value();
        Field<Object> newField = new Field<>(newName, valueToUse);
        fields.set(index, newField);
        return Result.Ok();
    }

    /**
     * Updates the value of an existing field by name.
     *
     * @param name     The field name to update
     * @param newValue The new value
     * @return true if updated successfully, false if field not found
     */
    public boolean setFieldValue(String name, Object newValue) {
        Field<?> field = getField(name);
        if (field == null) return false;

        // Replace with new value (preserving generic type via raw cast)
        Field<Object> newField = new Field<>(name, newValue);

        int index = fields.indexOf(field);
        fields.set(index, newField);
        return true;
    }

    /**
     * Renames an existing field and optionally updates its value.
     *
     * @param oldName  Original field name
     * @param newName  New field name
     * @param newValue Optional new value (null to keep existing)
     * @return Result: Ok() if renamed, Err() if old field not found
     */
    public Result renameField(String oldName, String newName, Object newValue) {
        Field<?> field = getField(oldName);
        if (field == null) {
            return Result.Err(Component.text("Field '" + oldName + "' not found"), false);
        }

        Object valueToUse = newValue != null ? newValue : field.value();
        Field<Object> newField = new Field<>(newName, valueToUse);

        int index = fields.indexOf(field);
        fields.set(index, newField);
        return Result.Ok();
    }

    /**
     * Updates multiple fields by name->value mappings.
     *
     * @param updates Map of fieldName -> newValue
     * @return Number of fields successfully updated
     */
    public int updateFields(Map<String, Object> updates) {
        int count = 0;
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            if (setFieldValue(entry.getKey(), entry.getValue())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets a field by its name.
     * <p>
     * This performs a linear search over the internal field list and returns
     * the first field whose {@link Field#name()} matches the given name.
     *
     * @param name the field name
     * @return the matching field, or {@code null} if no field with that name exists
     */
    public Field<?> getField(String name) {
        for (Field<?> field : fields) {
            if (field.name().equals(name)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Checks if a field with the given name exists in this configuration.
     *
     * @param name the field name to check
     * @return {@code true} if a field with the given name exists, {@code false} otherwise
     */
    public boolean exists(String name) {
        return getField(name) != null;
    }

    /**
     * Checks if the given field exists in this configuration.
     *
     * @param field the field to check for existence
     * @param <T>   the type of the field's value
     * @return true if the field exists, false otherwise
     */
    public <T> boolean exists(Field<T> field) {
        return getField(field) != null;
    }

    /**
     * Gets the value of the given field.
     * <p>
     * This is a convenience helper that returns {@link Field#value()}
     * while preserving the generic type where possible.
     *
     * @param field the field instance, may be {@code null}
     * @param <T>   the expected value type
     * @return the field's value, or {@code null} if the field is {@code null}
     */
    public <T> T getField(Field<T> field) {
        if (field == null) {
            return null;
        }
        return field.value();
    }

    /**
     * Serializes all fields and writes them to the backing YAML file, overwriting its contents.
     * <p>
     * The output format is standard YAML with one key/value entry per field.
     * Values are written using standard YAML scalar formatting.
     *
     * @return {@link Result#Ok()} on success, or {@link Result#Err(TextComponent, Exception, boolean)} on failure
     */
    public Result save() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            Field<?> field = fields.get(i);
            builder.append(field.name())
                    .append(": ")
                    .append(formatYamlValue(field.value()));
            if (i < fields.size() - 1) {
                builder.append("\n");
            }
        }

        try {
            Files.writeString(file.toPath(), builder.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Result.Err(
                    Component.text("Failed to write config '" + file.getName() + "'"),
                    new RuntimeException(e),
                    false
            );
        }

        return Result.Ok();
    }

    /**
     * Formats values for YAML output.
     * <p>
     * Strings are quoted only if they contain special characters, numbers/booleans/null are written as-is.
     *
     * @param value the value to format
     * @return a YAML-safe string representation
     */
    private String formatYamlValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        String str = value.toString();
        // Simple quoting for strings with special chars (space, :, #, etc.)
        if (str.contains(" ") || str.contains(":") || str.contains("#") || str.isEmpty()) {
            return "'" + str.replace("'", "''") + "'";
        }
        return str;
    }
}
