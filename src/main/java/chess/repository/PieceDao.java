package chess.repository;

import chess.model.piece.Piece;
import chess.model.position.Position;

import java.sql.SQLException;
import java.util.Map;

public class PieceDao {
    private final MySqlConnection mySqlConnection = MySqlConnection.INSTANCE;

    public static final PieceDao INSTANCE = new PieceDao();

    private PieceDao() {
    }

    public void createAll(Map<Position, Piece> board, int chessBoardId) {
        String query = "insert into piece(file, `rank`, type, chess_board_id, side) values (?, ?, ?, ?, ?)";
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            for (var position : board.keySet()) {
                var piece = board.get(position);
                var pieceMapper = PieceMapper.from(piece);
                preparedStatement.setString(1, position.getFile().getName());
                preparedStatement.setInt(2, position.getRank().getCoordinate());
                preparedStatement.setString(3, pieceMapper.type());
                preparedStatement.setInt(4, chessBoardId);
                preparedStatement.setString(5, pieceMapper.side());
                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(int chessBoardId, Position position, Piece piece) {
        String query = "update piece set type = ?, side = ? where file = ? and `rank` = ? and chess_board_id = ?";
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            var pieceMapper = PieceMapper.from(piece);
            preparedStatement.setString(1, pieceMapper.type());
            preparedStatement.setString(2, pieceMapper.side());
            preparedStatement.setString(3, position.getFile().getName());
            preparedStatement.setInt(4, position.getRank().getCoordinate());
            preparedStatement.setInt(5, chessBoardId);
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
