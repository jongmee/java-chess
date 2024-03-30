package chess.repository.dao;

import chess.model.piece.Piece;
import chess.model.position.Position;
import chess.repository.utility.StatementExecutor;
import chess.repository.utility.ParameterBinder;

import java.util.Map;

public class PieceDao {
    public static final PieceDao INSTANCE = new PieceDao();

    private final StatementExecutor statementExecutor = StatementExecutor.INSTANCE;

    private PieceDao() {
    }

    public void saveAll(Map<Position, Piece> board, long chessBoardId) {
        var query = "insert into piece(file, `rank`, type, chess_board_id, side) values (?, ?, ?, ?, ?)";
        ParameterBinder parameterBinder = preparedStatement -> {
            for (var entry : board.entrySet()) {
                var position = entry.getKey();
                var pieceMapper = PieceMapper.from(entry.getValue());
                preparedStatement.setString(1, position.getFile().getName());
                preparedStatement.setInt(2, position.getRank().getCoordinate());
                preparedStatement.setString(3, pieceMapper.typeAttribute());
                preparedStatement.setLong(4, chessBoardId);
                preparedStatement.setString(5, pieceMapper.sideAttribute());
                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
        };
        statementExecutor.executeBatch(query, parameterBinder);
    }

    public void update(long chessBoardId, Position position, Piece piece) {
        var query = "update piece set type = ?, side = ? where file = ? and `rank` = ? and chess_board_id = ?";
        var pieceMapper = PieceMapper.from(piece);
        ParameterBinder parameterBinder = preparedStatement -> {
            preparedStatement.setString(1, pieceMapper.typeAttribute());
            preparedStatement.setString(2, pieceMapper.sideAttribute());
            preparedStatement.setString(3, position.getFile().getName());
            preparedStatement.setInt(4, position.getRank().getCoordinate());
            preparedStatement.setLong(5, chessBoardId);
            preparedStatement.executeUpdate();
        };
        statementExecutor.executeUpdate(query, parameterBinder);
    }
}
