package chess.dao;

import chess.model.piece.Piece;
import chess.model.position.Position;
import chess.dao.util.MySqlConnector;
import chess.dao.util.ParameterBinder;
import chess.dao.util.StatementExecutor;

import java.util.Map;

public class PieceDao {
    private final StatementExecutor statementExecutor;

    public PieceDao(MySqlConnector mySqlConnector) {
        this.statementExecutor = new StatementExecutor(mySqlConnector);
    }

    public void saveAll(Map<Position, Piece> piecesWithPosition, long chessBoardId) {
        var query = "INSERT INTO piece(file, `rank`, type, chess_board_id, side) VALUES (?, ?, ?, ?, ?)";
        ParameterBinder parameterBinder = preparedStatement -> {
            for (var entry : piecesWithPosition.entrySet()) {
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
        var query = "UPDATE piece SET type = ?, side = ? WHERE file = ? AND `rank` = ? AND chess_board_id = ?";
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
