package chess.repository;

import chess.model.piece.Piece;
import chess.model.position.Position;

import java.util.Map;

public class PieceDao {
    public static final PieceDao INSTANCE = new PieceDao();

    private final StatementExecutor statementExecutor = StatementExecutor.INSTANCE;

    private PieceDao() {
    }

    public void createAll(Map<Position, Piece> board, int chessBoardId) {
        var query = "insert into piece(file, `rank`, type, chess_board_id, side) values (?, ?, ?, ?, ?)";
        ParameterBinder parameterBinder = preparedStatement -> {
            for (var entry : board.entrySet()) {
                var position = entry.getKey();
                var pieceMapper = PieceMapper.from(entry.getValue());
                preparedStatement.setString(1, position.getFile().getName());
                preparedStatement.setInt(2, position.getRank().getCoordinate());
                preparedStatement.setString(3, pieceMapper.type());
                preparedStatement.setInt(4, chessBoardId);
                preparedStatement.setString(5, pieceMapper.side());
                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
        };
        statementExecutor.executeBatch(query, parameterBinder);
    }

    public void update(int chessBoardId, Position position, Piece piece) {
        var query = "update piece set type = ?, side = ? where file = ? and `rank` = ? and chess_board_id = ?";
        var pieceMapper = PieceMapper.from(piece);
        ParameterBinder parameterBinder = preparedStatement -> {
            preparedStatement.setString(1, pieceMapper.type());
            preparedStatement.setString(2, pieceMapper.side());
            preparedStatement.setString(3, position.getFile().getName());
            preparedStatement.setInt(4, position.getRank().getCoordinate());
            preparedStatement.setInt(5, chessBoardId);
            preparedStatement.executeUpdate();
        };
        statementExecutor.executeUpdate(query, parameterBinder);
    }
}
