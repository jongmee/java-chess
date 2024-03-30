package chess.repository.dao;

import chess.model.board.Turn;
import chess.repository.utility.ParameterBinder;
import chess.repository.utility.ResultSetMapper;
import chess.repository.utility.StatementExecutor;

import java.util.Optional;

public class TurnDao {
    public static final TurnDao INSTANCE = new TurnDao();

    private final StatementExecutor statementExecutor = StatementExecutor.INSTANCE;

    private TurnDao() {
    }

    public void save(long chessBoardId, Turn turn) {
        var query = "insert into turn(side, chess_board_id) values(?, ?)";
        var side = TurnMapper.mapToSideAttribute(turn);
        ParameterBinder parameterBinder = preparedStatement -> {
            preparedStatement.setString(1, side);
            preparedStatement.setLong(2, chessBoardId);
        };
        statementExecutor.executeUpdate(query, parameterBinder);
    }

    public Optional<Turn> findByChessBoardId(long chessBoardId) {
        var query = "select * from turn where chess_board_id = ?";
        ParameterBinder parameterBinder = preparedStatement -> preparedStatement.setLong(1, chessBoardId);
        ResultSetMapper<Optional<Turn>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                var sideAttribute = resultSet.getString("side");
                Turn turn = TurnMapper.mapToTurn(sideAttribute);
                return Optional.of(turn);
            }
            return Optional.empty();
        };
        return statementExecutor.executeQuery(query, parameterBinder, resultSetMapper);
    }
}
