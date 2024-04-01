package chess.repository.util;

import chess.repository.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnector {
    public static final MySqlConnector PRODUCT_CONNECTION = new MySqlConnector("chess");
    public static final MySqlConnector TEST_CONNECTION = new MySqlConnector("chess_test");

    private static final String OPTION = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String SERVER = "localhost:13306";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private final String database;

    private MySqlConnector(String database) {
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
