package chess.repository.dao;

import chess.model.board.Turn;
import chess.model.piece.Side;
import chess.repository.util.MySqlConnector;
import chess.repository.util.ParameterBinder;
import chess.repository.util.ResultSetMapper;
import chess.repository.util.StatementExecutor;

import java.util.Optional;

public class TurnDao {
    private final StatementExecutor statementExecutor;

    public TurnDao(MySqlConnector mySqlConnector) {
        this.statementExecutor = new StatementExecutor(mySqlConnector);
    }

    public void save(long chessBoardId, Turn turn) {
        var query = "INSERT INTO turn(side, chess_board_id) VALUES (?, ?)";
        var side = turn.getSide().name();
        ParameterBinder parameterBinder = preparedStatement -> {
            preparedStatement.setString(1, side);
            preparedStatement.setLong(2, chessBoardId);
        };
        statementExecutor.executeUpdate(query, parameterBinder);
    }

    public void delete(long chessBoardId) {
        var query = "DELETE FROM turn WHERE chess_board_id = ?";
        ParameterBinder parameterBinder = preparedStatement -> preparedStatement.setLong(1, chessBoardId);
        statementExecutor.executeUpdate(query, parameterBinder);
    }

    public Optional<Turn> findByChessBoardId(long chessBoardId) {
        var query = "SELECT * FROM turn WHERE chess_board_id = ?";
        ParameterBinder parameterBinder = preparedStatement -> preparedStatement.setLong(1, chessBoardId);
        ResultSetMapper<Optional<Turn>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                var sideAttribute = resultSet.getString("side");
                Turn turn = Turn.from(Side.valueOf(sideAttribute));
                return Optional.of(turn);
            }
            return Optional.empty();
        };
        return statementExecutor.executeQuery(query, parameterBinder, resultSetMapper);
    }
}
