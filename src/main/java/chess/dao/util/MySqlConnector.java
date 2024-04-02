package chess.dao.util;

import chess.dao.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class MySqlConnector {
    private static final String OPTION = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String SERVER = "localhost:13306";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    private final String database;

    protected MySqlConnector(String database) {
        this.database = database;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + database + OPTION, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("DB 연결 오류:" + e.getMessage());
        }
    }
}
