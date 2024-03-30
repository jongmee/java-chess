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

    public Optional<Long> save() {
        var query = "insert into chess_board values()";
        String[] keys = {"chess_board_id"};
        ResultSetMapper<Optional<Long>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                return Optional.of(resultSet.getLong(1));
            }
            return Optional.empty();
        };
        return statementExecutor.executeUpdate(query, keys, resultSetMapper);
    }

    public Optional<ChessBoard> findLatest() {
        var query = "select p.* from piece p " +
                "join chess_board cb " +
                "on p.chess_board_id = cb.chess_board_id " +
                "where cb.created_at = (select max(created_at) from chess_board)";
        ResultSetMapper<Optional<ChessBoard>> resultSetMapper = resultSet -> {
            Map<Position, Piece> board = new HashMap<>();
            long chessBoardId = -1;
            while (resultSet.next()) {
                var fileAttribute = resultSet.getString("file");
                var rankAttribute = resultSet.getInt("rank");
                Position position = Position.of(File.from(fileAttribute), Rank.from(rankAttribute));
                var typeAttribute = resultSet.getString("type");
                var sideAttribute = resultSet.getString("side");
                Piece piece = PieceMapper.mapToPiece(typeAttribute, sideAttribute);
                board.put(position, piece);
                chessBoardId = resultSet.getLong("chess_board_id");
            }
            if (chessBoardId == -1) {
                return Optional.empty();
            }
            return Optional.of(new ChessBoard(chessBoardId, board));
        };
        return statementExecutor.executeQuery(query, emptyBinder(), resultSetMapper);
    }

    private ParameterBinder emptyBinder() {
        return preparedStatement -> {
        };
    }
}
