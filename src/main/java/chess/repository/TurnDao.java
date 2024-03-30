package chess.repository;

import chess.model.board.Turn;

import java.util.Optional;

public class TurnDao {
    public static final TurnDao INSTANCE = new TurnDao();

    private final StatementExecutor statementExecutor = StatementExecutor.INSTANCE;

    private TurnDao() {
    }

    public void save(int chessBoardId, Turn turn) {
        var query = "insert into turn(side, chess_board_id) values(?, ?)";
        var side = TurnMapper.findSideByTurn(turn);
        ParameterBinder parameterBinder = preparedStatement -> {
            preparedStatement.setString(1, side);
            preparedStatement.setInt(2, chessBoardId);
        };
        statementExecutor.executeUpdate(query, parameterBinder);
    }

    public Optional<Turn> findByChessBoardId(int chessBoardId) {
        var query = "select * from turn where chess_board_id = ?";
        ParameterBinder parameterBinder = preparedStatement -> preparedStatement.setInt(1, chessBoardId);
        ResultSetMapper<Optional<Turn>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                var side = resultSet.getString("side");
                Turn turn = TurnMapper.findTurnBySide(side);
                return Optional.of(turn);
            }
            return Optional.empty();
        };
        return statementExecutor.executeQuery(query, parameterBinder, resultSetMapper);
    }
}
