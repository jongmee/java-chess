package chess.repository;

import chess.model.board.ChessBoard;
import chess.model.piece.Piece;
import chess.model.position.File;
import chess.model.position.Position;
import chess.model.position.Rank;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChessBoardDao {
    private final MySqlConnection mySqlConnection = MySqlConnection.INSTANCE;

    public static final ChessBoardDao INSTANCE = new ChessBoardDao();

    private ChessBoardDao() {
    }

    public Optional<Integer> create() {
        String query = "insert into chess_board values()";
        String primaryKey[] = {"chess_board_id"};
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query, primaryKey)) {
            preparedStatement.executeUpdate();
            var resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return Optional.of(resultSet.getInt(1));
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public ChessBoard findById(int chessBoardId) {
        String query = "select * from piece where chess_board_id = ?";
        try (var connection = mySqlConnection.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, chessBoardId);
            var resultSet = preparedStatement.executeQuery();
            Map<Position, Piece> board = new HashMap<>();
            while (resultSet.next()) {
                Position position = Position.of(File.from(resultSet.getString("file")), Rank.from(resultSet.getInt("rank")));
                Piece piece = PieceMapper.findPieceByTypeAndSide(resultSet.getString("type"), resultSet.getString("side"));
                board.put(position, piece);
            }
            return new ChessBoard(board);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
