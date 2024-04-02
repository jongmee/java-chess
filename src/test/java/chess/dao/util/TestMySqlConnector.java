package chess.dao.util;

public class TestMySqlConnector extends MySqlConnector {
    public static final TestMySqlConnector INSTANCE = new TestMySqlConnector();

    private TestMySqlConnector() {
        super("chess_test");
    }
}
