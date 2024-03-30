package chess.repository.dao;

import chess.model.board.ChessBoard;
import chess.model.evaluation.GameResult;
import chess.model.piece.Piece;
import chess.model.position.File;
import chess.model.position.Position;
import chess.model.position.Rank;
import chess.repository.dto.GameResultDto;
import chess.repository.utility.ParameterBinder;
import chess.repository.utility.ResultSetMapper;
import chess.repository.utility.StatementExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ChessBoardDao {
    public static final ChessBoardDao INSTANCE = new ChessBoardDao();

    private final StatementExecutor statementExecutor = StatementExecutor.INSTANCE;

    private ChessBoardDao() {
    }

    public Optional<Long> save() {
        var query = "insert into chess_board(game_result) values(?)";
        String[] keys = {"chess_board_id"};
        ParameterBinder parameterBinder = preparedStatement ->
                preparedStatement.setString(1, GameResult.IN_PROGRESS.name());
        ResultSetMapper<Optional<Long>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                return Optional.of(resultSet.getLong(1));
            }
            return Optional.empty();
        };
        return statementExecutor.executeUpdate(query, keys, parameterBinder, resultSetMapper);
    }

    public Optional<ChessBoard> findLatest() {
        var query = "select p.* from chess_board cb " +
                "join piece p " +
                "on p.chess_board_id = cb.chess_board_id " +
                "where cb.created_at = (select max(created_at) from chess_board) and cb.game_result = ?";
        ParameterBinder parameterBinder = preparedStatement ->
                preparedStatement.setString(1, GameResult.IN_PROGRESS.name());
        ResultSetMapper<Optional<ChessBoard>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                return convertChessBoard(resultSet);
            }
            return Optional.empty();
        };
        return statementExecutor.executeQuery(query, parameterBinder, resultSetMapper);
    }

    private Optional<ChessBoard> convertChessBoard(ResultSet resultSet) throws SQLException {
        Map<Position, Piece> board = new HashMap<>();
        long chessBoardId = resultSet.getLong("chess_board_id");
        do {
            Position position = convertPosition(resultSet);
            Piece piece = convertPiece(resultSet);
            board.put(position, piece);
        } while (resultSet.next());
        return Optional.of(new ChessBoard(chessBoardId, board));
    }

    private Position convertPosition(ResultSet resultSet) throws SQLException {
        var fileAttribute = resultSet.getString("file");
        var rankAttribute = resultSet.getInt("rank");
        return Position.of(File.from(fileAttribute), Rank.from(rankAttribute));
    }

    private Piece convertPiece(ResultSet resultSet) throws SQLException {
        var typeAttribute = resultSet.getString("type");
        var sideAttribute = resultSet.getString("side");
        return PieceMapper.mapToPiece(typeAttribute, sideAttribute);
    }

    public void updateGameResult(long chessBoardId, GameResult gameResult) {
        var query = "update chess_board set game_result = ? where chess_board_id = ?";
        ParameterBinder parameterBinder = preparedStatement -> {
            preparedStatement.setString(1, gameResult.name());
            preparedStatement.setLong(2, chessBoardId);
        };
        statementExecutor.executeUpdate(query, parameterBinder);
    }

    public List<GameResultDto> findAllGameResult() {
        var query = "select * from chess_board where game_result != ? order by created_at desc";
        ParameterBinder parameterBinder = preparedStatement ->
                preparedStatement.setString(1, GameResult.IN_PROGRESS.name());
        ResultSetMapper<List<GameResultDto>> resultSetMapper = resultSet -> {
            List<GameResultDto> gameResultDtos = new ArrayList<>();
            while (resultSet.next()) {
                var gameResultDto = convertGameResultDto(resultSet);
                gameResultDtos.add(gameResultDto);
            }
            return gameResultDtos;
        };
        return statementExecutor.executeQuery(query, parameterBinder, resultSetMapper);
    }

    private GameResultDto convertGameResultDto(ResultSet resultSet) throws SQLException {
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        var gameResult = GameResult.valueOf(resultSet.getString("game_result"));
        return new GameResultDto(createdAt, gameResult);
    }
}
