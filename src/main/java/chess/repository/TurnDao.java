package chess.repository;

import chess.model.board.Turn;
import chess.model.piece.Side;

import java.sql.SQLException;
import java.util.Optional;

public class TurnDao {
    private final MySqlConnection mySqlConnection = MySqlConnection.INSTANCE;

    public static final TurnDao INSTANCE = new TurnDao();

    private TurnDao() {
    }

    public void save(int chessBoardId, Turn turn) {
        String query = "insert into turn(side, chess_board_id) values(?, ?)";
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            var side = TurnMapper.findSideByTurn(turn);
            preparedStatement.setString(1, side);
            preparedStatement.setInt(2, chessBoardId);
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Turn> findByChessBoardId(int chessBoardId) {
        String query = "select * from turn where chess_board_id = ?";
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, chessBoardId);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var side = resultSet.getString("side");
                Turn turn = TurnMapper.findTurnBySide(side);
                return Optional.of(turn);
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
