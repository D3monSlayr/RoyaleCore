package dev.royalcore.api.db;

import com.google.gson.Gson;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private final String dbPath;
    private final ExecutorService dbExecutor;
    private final Plugin plugin;
    private final Map<String, DatabaseSession> tableCache = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private Connection connection;

    /**
     * @param dbPath  Path to database, e.g. "plugins/YourPlugin/database.db"
     * @param plugin  Main plugin instance for logging
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
     * Opens (or creates) the SQLite connection and applies PRAGMA options for best PaperMC usage.
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
     * Should be called in your plugin's onDisable().
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
        } catch (Exception ignored) {}
    }

    /**
     * Get (and cache) a database session for a logical table.
     * Automatically validates table names (letters, numbers, and underscores only).
     * All physical table names in DB will be prefixed with "data_".
     */
    public DatabaseSession use(String tableName) {
        if (!tableName.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        return tableCache.computeIfAbsent(tableName, tn -> new DatabaseSession("data_" + tn));
    }

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
     * Table/session abstraction, all methods are async and safe.
     */
    public class DatabaseSession {
        private final String tableName;

        private DatabaseSession(String tableName) {
            this.tableName = tableName;
        }

        /**
         * Ensures the underlying table exists (must always be called before writing/reading).
         */
        public CompletableFuture<Void> ensureExists() {
            return Database.this.runAsync(() -> {
                String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (key TEXT PRIMARY KEY, value TEXT);";
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                }
                return null;
            }, "ensureExists: " + tableName);
        }

        /**
         * Writes any serializable value to a key (supports primitives/POJOs).
         */
        public CompletableFuture<Void> write(String key, Object value) {
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
         * Reads the string value (or null if not set).
         */
        public CompletableFuture<String> read(String key) {
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
         * Reads an integer value, logs a warning if the DB contains malformed data.
         */
        public CompletableFuture<Integer> readInt(String key) {
            return read(key).thenApply(str -> {
                try { return str != null ? Integer.parseInt(str) : null; }
                catch (Exception e) {
                    plugin.getLogger().warning("Malformed integer in table " + tableName + ": key=" + key + ", value=" + str);
                    return null;
                }
            });
        }

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
         * Reads a double value, logs a warning if the DB contains malformed data.
         */
        public CompletableFuture<Double> readDouble(String key) {
            return read(key).thenApply(str -> {
                try { return str != null ? Double.parseDouble(str) : null; }
                catch (Exception e) {
                    plugin.getLogger().warning("Malformed double in table " + tableName + ": key=" + key + ", value=" + str);
                    return null;
                }
            });
        }

        /**
         * Reads a POJO (as previously serialized by Gson).
         */
        public <T> CompletableFuture<T> readObject(String key, Class<T> type) {
            return read(key).thenApply(str -> {
                try { return str != null ? gson.fromJson(str, type) : null; }
                catch (Exception e) {
                    plugin.getLogger().warning("Malformed JSON in table " + tableName + ": key=" + key + ", value=" + str);
                    return null;
                }
            });
        }

        /**
         * Deletes the entire table (use with caution).
         */
        public CompletableFuture<Void> delete() {
            return Database.this.runAsync(() -> {
                String sql = "DROP TABLE IF EXISTS " + tableName + ";";
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                }
                return null;
            }, "delete: " + tableName);
        }

        private boolean isPrimitive(Object value) {
            return value instanceof String || value instanceof Number || value instanceof Boolean;
        }
    }
}

