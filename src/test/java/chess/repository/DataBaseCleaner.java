package chess.repository;

import chess.repository.util.MySqlConnector;

import java.sql.SQLException;
import java.util.List;

public class DataBaseCleaner {
    private static final List<String> QUERIES = List.of(
            "set foreign_key_checks = 0",
            "truncate chess_board",
            "truncate piece",
            "truncate turn",
            "set foreign_key_checks = 1"
    );

    private final MySqlConnector mySqlConnector = MySqlConnector.TEST_CONNECTION;

    public void truncateTables() {
        try (var connection = mySqlConnector.getConnection();
             var statement = connection.createStatement()) {
            for (String query : QUERIES) {
                statement.addBatch(query);
            }
            statement.executeBatch();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
