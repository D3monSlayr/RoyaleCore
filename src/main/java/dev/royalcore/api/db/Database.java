package dev.royalcore.api.db;

import com.google.gson.Gson;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Simple asynchronous SQLite database helper for RoyaleCore.
 * <p>
 * This class manages a single SQLite connection and provides table-scoped
 * {@link DatabaseSession} instances for async CRUD-style operations.
 */
public class Database {
    private final String dbPath;
    private final ExecutorService dbExecutor;
    private final Plugin plugin;
    private final Map<String, DatabaseSession> tableCache = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private Connection connection;

    /**
     * Creates a new database helper for the given path.
     *
     * @param dbPath path to database, e.g. {@code "plugins/YourPlugin/database.db"}
     * @param plugin main plugin instance for logging and lifecycle integration
     */
    public Database(String dbPath, Plugin plugin) {
        this.dbPath = dbPath;
        this.plugin = plugin;
        this.dbExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "DatabaseThread");
            t.setDaemon(true); // Server shuts down cleanly even if shutdown() isn't called
            return t;
        });
    }

    /**
     * Opens a connection to the configured SQLite database.
     *
     * @throws SQLException           if a database access error occurs
     * @throws ClassNotFoundException if the SQLite JDBC driver class cannot be found
     */
    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

        // Faster, safer disk writes for a Minecraft plugin!
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode = WAL;");
            stmt.execute("PRAGMA synchronous = NORMAL;");
        }
    }

    /**
     * Waits for all async DB work to finish and closes the database connection.
     * <p>
     * Should be called in your plugin's {@code onDisable()}.
     */
    public void shutdown() {
        dbExecutor.shutdown();
        try {
            if (!dbExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                plugin.getLogger().warning("Database executor did not terminate in time!");
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Returns a session bound to the given logical table name.
     * <p>
     * The physical table name is prefixed with {@code data_} and validated
     * to contain only alphanumeric characters and underscores.
     *
     * @param tableName the logical table name to operate on
     * @return a {@link DatabaseSession} for the given table
     * @throws IllegalArgumentException if the table name contains invalid characters
     */
    public DatabaseSession use(String tableName) {
        if (!tableName.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        return tableCache.computeIfAbsent(tableName, tn -> new DatabaseSession("data_" + tn));
    }

    /**
     * Submits a database task to run asynchronously on the dedicated executor.
     *
     * @param task    the callable to execute
     * @param context a short description used in error logging
     * @param <T>     the result type of the callable
     * @return a {@link CompletableFuture} representing the task result
     */
    protected <T> CompletableFuture<T> runAsync(Callable<T> task, String context) {
        CompletableFuture<T> future = new CompletableFuture<>();
        dbExecutor.submit(() -> {
            try {
                future.complete(task.call());
            } catch (Exception e) {
                plugin.getLogger().severe("Database operation failed (" + context + "): " + e.getMessage());
                future.completeExceptionally(
                        new RuntimeException("Database operation failed (" + context + ")", e)
                );
            }
        });
        return future;
    }

    /**
     * Table/session abstraction; all methods are asynchronous and thread-safe.
     */
    public class DatabaseSession {
        private final String tableName;

        /**
         * Creates a new session bound to a specific table name.
         *
         * @param tableName the physical table name in the database
         */
        private DatabaseSession(String tableName) {
            this.tableName = tableName;
        }

        /**
         * Ensures that the underlying table exists, creating it if necessary.
         *
         * @return a future that completes when the table has been checked or created
         */
        public @Nullable CompletableFuture<Void> ensureExists() {
            return Database.this.runAsync(() -> {
                String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (key TEXT PRIMARY KEY, value TEXT);";
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                }
                return null;
            }, "ensureExists: " + tableName);
        }

        /**
         * Writes a key-value pair to the table, replacing any existing entry.
         * <p>
         * Primitive-like types are stored as plain strings, all other objects
         * are serialized as JSON using Gson.
         *
         * @param key   the key to write
         * @param value the value to store (primitive or POJO)
         * @return a future that completes when the write finishes
         */
        public @Nullable CompletableFuture<Void> write(String key, Object value) {
            return Database.this.runAsync(() -> {
                String sql = "INSERT OR REPLACE INTO " + tableName + " (key, value) VALUES (?, ?);";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, key);
                    pstmt.setString(2, isPrimitive(value) ? String.valueOf(value) : gson.toJson(value));
                    pstmt.executeUpdate();
                }
                return null;
            }, "write: " + tableName + " key=" + key);
        }

        /**
         * Reads a string value by key.
         *
         * @param key the key to read
         * @return a future with the stored string value, or {@code null} if missing
         */
        public @Nullable CompletableFuture<String> read(String key) {
            return Database.this.runAsync(() -> {
                String sql = "SELECT value FROM " + tableName + " WHERE key = ?;";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, key);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("value");
                        }
                        return null;
                    }
                }
            }, "read: " + tableName + " key=" + key);
        }

        /**
         * Reads an integer value, logging a warning if the stored data is malformed.
         *
         * @param key the key to read
         * @return a future with the parsed integer value, or {@code null} if missing or invalid
         */
        public CompletableFuture<Integer> readInt(String key) {
            return read(key).thenApply(str -> {
                try {
                    return str != null ? Integer.parseInt(str) : null;
                } catch (Exception e) {
                    plugin.getLogger().warning("Malformed integer in table " + tableName + ": key=" + key + ", value=" + str);
                    return null;
                }
            });
        }

        /**
         * Reads all entries in this table and deserializes them as the given type.
         *
         * @param type the target class for deserialization
         * @param <T>  the result object type
         * @return a future with a map of keys to deserialized objects
         */
        public <T> CompletableFuture<Map<String, T>> readAll(Class<T> type) {
            return Database.this.runAsync(() -> {
                String sql = "SELECT key, value FROM " + tableName + ";";
                Map<String, T> result = new HashMap<>();
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        String key = rs.getString("key");
                        String value = rs.getString("value");
                        T obj = null;
                        try {
                            obj = gson.fromJson(value, type);
                        } catch (Exception e) {
                            plugin.getLogger().warning("Failed to deserialize entry for key=" + key + ": " + e.getMessage());
                        }
                        if (obj != null) result.put(key, obj);
                    }
                }
                return result;
            }, "readAll: " + tableName);
        }

        /**
         * Reads a double value, logging a warning if the stored data is malformed.
         *
         * @param key the key to read
         * @return a future with the parsed double value, or {@code null} if missing or invalid
         */
        public @Nullable CompletableFuture<Double> readDouble(String key) {
            return read(key).thenApply(str -> {
                try {
                    return str != null ? Double.parseDouble(str) : null;
                } catch (Exception e) {
                    plugin.getLogger().warning("Malformed double in table " + tableName + ": key=" + key + ", value=" + str);
                    return null;
                }
            });
        }

        /**
         * Reads a POJO that was previously serialized by Gson.
         *
         * @param key  the key to read
         * @param type the target class for deserialization
         * @param <T>  the result object type
         * @return a future with the deserialized object, or {@code null} if missing or invalid
         */
        public @Nullable <T> CompletableFuture<T> readObject(String key, Class<T> type) {
            return read(key).thenApply(str -> {
                try {
                    return str != null ? gson.fromJson(str, type) : null;
                } catch (Exception e) {
                    plugin.getLogger().warning("Malformed JSON in table " + tableName + ": key=" + key + ", value=" + str);
                    return null;
                }
            });
        }

        /**
         * Deletes the entire table (use with caution).
         *
         * @return a future that completes when the table has been dropped
         */
        public @Nullable CompletableFuture<Void> delete() {
            return Database.this.runAsync(() -> {
                String sql = "DROP TABLE IF EXISTS " + tableName + ";";
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                }
                return null;
            }, "delete: " + tableName);
        }

        /**
         * Determines whether a value can be stored as a plain string without JSON.
         *
         * @param value the value to inspect
         * @return {@code true} if the value is a String, Number or Boolean, otherwise {@code false}
         */
        private boolean isPrimitive(Object value) {
            return value instanceof String || value instanceof Number || value instanceof Boolean;
        }
    }
}
