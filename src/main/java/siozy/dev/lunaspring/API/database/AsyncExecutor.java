package siozy.dev.lunaspring.API.database;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import siozy.dev.lunaspring.LunaSpring;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class AsyncExecutor {
    private final ExecutorService executor;
    private final ConnectionPool connectionPool;

    public AsyncExecutor(ConfigurationSection section) {
        this.executor = Executors.newFixedThreadPool(section.getInt("asyncThreadPoolSize"));
        this.connectionPool = new ConnectionPool(section);
    }

    public CompletableFuture<Void> completableExecute(String query, Object... params) {
        return CompletableFuture.runAsync(() -> {
            try {
                connectionPool.executeUpdate(query, 1, params);
            } catch (SQLException e) {
                throw new RuntimeException("DB error: " + query, e);
            }
        }, executor).exceptionally(ex -> {
            LunaSpring.getInstance().warning("&cАсинхронный запрос " + query + " упал: " + ex);
            return null;
        });
    }

    public <T> CompletableFuture<List<T>> completableQuery(String query, ResultSetHandler<T> handler, Object... params) {
        return CompletableFuture.supplyAsync(() ->
                executeQuery(query, handler, params), executor
        );
    }

    @Deprecated
    public void executeAsync(String query, Object... params) {
        executor.submit(() -> {
            try {
                connectionPool.executeUpdate(query, 1, params);
            } catch (SQLException e) {
                LunaSpring.getInstance().warning("&cАсинхронный запрос " + query + " упал: " + e);
            }
        });
    }

    public void executeSync(String query, Object... params) {
        try {
            connectionPool.executeUpdate(query, 1, params);
        } catch (SQLException e) {
            LunaSpring.getInstance().warning("&cСинхронный запрос " + query + " упал: " + e);
        }
    }

    public <T> List<T> executeQuery(String query, ResultSetHandler<T> handler, Object... params) {
        try {
            return connectionPool.executeQuery(query, handler, params);
        } catch (SQLException e) {
            LunaSpring.getInstance().warning("&cGET запрос " + query + " упал: " + e);
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void shutdown() {
        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        connectionPool.closeAll();
    }
}
