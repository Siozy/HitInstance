package siozy.dev.lunaspring.API.database;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.LunaSpring;
import siozy.dev.lunaspring.self.configuration.LSConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Getter
public class ConnectionPool {
    private final BlockingQueue<Connection> connectionQueue;
    private final int maxRetries;
    private final String url;
    private final String user;
    private final String password;
    private final int queryTimeout;
    private final int validationTimeout;
    private final int maxRows;

    @SneakyThrows
    ConnectionPool(ConfigurationSection section) {
        if (section == null) throw new IllegalArgumentException("ConfigurationSection не может быть null!");

        String driverClass = Objects.requireNonNull(section.getString("driverClass"), "Driver Class базы данных не может быть null!");
        this.url = Objects.requireNonNull(section.getString("url"), "URL базы данных не может быть null!");
        this.user = section.getString("username");
        this.password = section.getString("password");

        this.maxRetries = this.validatePositive(section.getInt("maxRetries"), "maxRetries");
        this.maxRows = this.validatePositive(section.getInt("maxRows", 100_000), "maxRows");
        int poolSize = this.validatePositive(section.getInt("poolSize"), "poolSize");
        this.queryTimeout = this.validatePositive(section.getInt("queryTimeout", 30), "queryTimeout");
        this.validationTimeout = this.validatePositive(section.getInt("validationTimeout", 2), "validationTimeout");

        Class.forName(driverClass);
        this.connectionQueue = new ArrayBlockingQueue<>(poolSize);

        initializePool(poolSize);
    }

    private void initializePool(int poolSize) throws SQLException {
        SQLException lastError = null;
        int createdConnections = 0;

        for (int i = 0; i < poolSize; i++) {
            try {
                this.connectionQueue.add(this.createNewConnection());
                createdConnections++;
            } catch (SQLException e) {
                lastError = e;
                LunaSpring.getInstance().warning("Ошибка при создании соединения: " + e.getMessage());
            }
        }

        if (createdConnections == 0 && lastError != null) {
            throw new SQLException("Не удалось создать ни одного соединения", lastError);
        }
    }

    private int validatePositive(int value, String paramName) {
        if (value <= 0) throw new IllegalArgumentException(paramName + " должен быть положительным числом!");
        return value;
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection conn = connectionQueue.take();

            if (!isConnectionValid(conn)) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LunaSpring.getInstance().warning("Ошибка при закрытии неисправного соединения: " + e.getMessage());
                }
                conn = createNewConnection();
            }
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Поток был прерван при ожидании соединения", e);
        }
    }

    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(validationTimeout);
        } catch (SQLException e) {
            return false;
        }
    }

    private Connection createNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);

        conn.setAutoCommit(true);
        return conn;
    }

    @SneakyThrows
    private void setParameters(PreparedStatement stmt, Object[] params) {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            int paramIndex = i + 1;

            try {
                if (param == null) {
                    stmt.setNull(paramIndex, Types.NULL);
                } else if (param instanceof Integer) {
                    stmt.setInt(paramIndex, (Integer) param);
                } else if (param instanceof Long) {
                    stmt.setLong(paramIndex, (Long) param);
                } else {
                    stmt.setObject(paramIndex, param);
                }
            } catch (SQLException e) {
                throw new SQLException(String.format("Ошибка вставки параметра %d (value: %s)", paramIndex, param), e);
            }
        }
    }

    @SneakyThrows
    public void releaseConnection(Connection conn) {
        if (conn == null) return;

        try {
            if (!isConnectionValid(conn)) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LunaSpring.getInstance().warning("Ошибка при закрытии невалидного соединения: " + e.getMessage());
                }
                conn = createNewConnection();
                LunaSpring.getInstance().warning(LSConfig.getString("messages.db.invalidConnection"));
            }

            if (!connectionQueue.offer(conn)) {
                LunaSpring.getInstance().warning("Пул соединений переполнен - соединение будет закрыто");
                conn.close();
            }
        } catch (SQLException e) {
            LunaSpring.getInstance().warning("Ошибка при освобождении соединения: " + e.getMessage());
            try {
                conn.close();
            } catch (SQLException ex) {
                LunaSpring.getInstance().warning("Ошибка при закрытии соединения: " + ex.getMessage());
            }
            throw e;
        }
    }



    @SneakyThrows
    public void executeUpdate(String query, int currentAttempt, Object... params) throws SQLException {
        if (currentAttempt > this.maxRetries) {
            throw new SQLException(String.format("Превышено максимальное количество попыток (%d) выполнения запроса", this.maxRetries));
        }
        Connection connection = this.getConnection();

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                setParameters(stmt, params);
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }

            if (currentAttempt < maxRetries) {
                LunaSpring.getInstance().warning(Utils.applyReplacements(
                        LSConfig.getString("messages.DBnewAttempt"),
                        "query-%-" + query,
                        "attempt-%-" + (currentAttempt + 1)
                ));

                try {
                    Thread.sleep(1000L);
                    if (!this.isConnectionValid(connection)) {
                        connection.close();
                        connection = createNewConnection();
                    }
                    this.executeUpdate(query, currentAttempt + 1, params);
                    return;

                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Поток прерван при повторной попытке выполнения запроса", ie);
                }
            }

            throw e;
        } finally {
            try {
                connection.setAutoCommit(true);
            }
            catch (SQLException e) {
                LunaSpring.getInstance().warning("Ошибка при восстановлении autoCommit: " + e.getMessage());
                connection.rollback();
            }
            this.releaseConnection(connection);
        }
    }

    /**
     * Отправляет запрос к БД и возвращает данные по переданному шаблону
     * @param sql - Запрос SQL к БД
     * @param handler - Шаблон по которому создаются объекты
     * @param params - параметры для вставки в SQL выражение (По порядку)
     * @return Список объектов типа <T>
     * @param <T> - Объект, создание которого описывает ResultSetHandler
     */
    public <T> List<T> executeQuery(String sql,
                                    ResultSetHandler<T> handler,
                                    Object... params) throws SQLException {
        Objects.requireNonNull(sql, "SQL не может быть null!");
        Objects.requireNonNull(handler, "ResultSetHandler не может быть null!");
        Connection connection = this.getConnection();
        Objects.requireNonNull(connection, "Connection не может быть null!");

        List<T> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(
                sql,
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY,
                ResultSet.HOLD_CURSORS_OVER_COMMIT)) {

            stmt.setQueryTimeout(queryTimeout);
            this.setParameters(stmt, params);

            try (ResultSet rs = stmt.executeQuery()) {
                int rowCount = 0;
                while (rs.next()) {
                    if (rowCount >= maxRows) {
                        throw new SQLException("Превышен лимит результатов (" + maxRows + " строк)");
                    }

                    try {
                        result.add(handler.handle(rs));
                        rowCount++;
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка обработки строки " + rowCount, e);
                    }
                }

                return result;
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка выполнения запроса: " + StringUtils.abbreviate(sql, 200), e);
        } finally {
            this.releaseConnection(connection);
        }
    }

    @SneakyThrows
    public void closeAll() {
        SQLException lastError = null;
        while (!connectionQueue.isEmpty()) {
            Connection connection = connectionQueue.poll();
            if (connection != null) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    lastError = e;
                }
            }
        }
        if (lastError != null) {
            throw lastError;
        }
    }
}