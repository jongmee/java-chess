package chess.repository.dao;

import chess.model.board.ChessBoard;
import chess.model.piece.Piece;
import chess.model.position.File;
import chess.model.position.Position;
import chess.model.position.Rank;
import chess.repository.utility.ParameterBinder;
import chess.repository.utility.ResultSetMapper;
import chess.repository.utility.StatementExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChessBoardDao {
    public static final ChessBoardDao INSTANCE = new ChessBoardDao();

    private final StatementExecutor statementExecutor = StatementExecutor.INSTANCE;

    private ChessBoardDao() {
    }

    public Optional<Integer> save() {
        var query = "insert into chess_board values()";
        String[] keys = {"chess_board_id"};
        ResultSetMapper<Optional<Integer>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                return Optional.of(resultSet.getInt(1));
            }
            return Optional.empty();
        };
        return statementExecutor.executeUpdate(query, keys, resultSetMapper);
    }

    public ChessBoard findById(int chessBoardId) {
        var query = "select * from piece where chess_board_id = ?";
        ParameterBinder parameterBinder = preparedStatement -> preparedStatement.setInt(1, chessBoardId);
        ResultSetMapper<ChessBoard> resultSetMapper = resultSet -> {
            Map<Position, Piece> board = new HashMap<>();
            while (resultSet.next()) {
                var fileAttribute = resultSet.getString("file");
                var rankAttribute = resultSet.getInt("rank");
                Position position = Position.of(File.from(fileAttribute), Rank.from(rankAttribute));
                var typeAttribute = resultSet.getString("type");
                var sideAttribute = resultSet.getString("side");
                Piece piece = PieceMapper.mapToPiece(typeAttribute, sideAttribute);
                board.put(position, piece);
            }
            return new ChessBoard(board);
        };
        return statementExecutor.executeQuery(query, parameterBinder, resultSetMapper);
    }
}
