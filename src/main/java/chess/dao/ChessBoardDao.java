package chess.dao;

import chess.dao.dto.GameResultDto;
import chess.dao.dto.LatestChessBoardDto;
import chess.dao.dto.NewChessBoardDto;
import chess.dao.util.MySqlConnector;
import chess.dao.util.ParameterBinder;
import chess.dao.util.ResultSetMapper;
import chess.dao.util.StatementExecutor;
import chess.model.board.ChessBoard;
import chess.model.board.Turn;
import chess.model.evaluation.GameResult;
import chess.model.piece.Piece;
import chess.model.piece.Side;
import chess.model.position.File;
import chess.model.position.Position;
import chess.model.position.Rank;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ChessBoardDao {
    private final StatementExecutor statementExecutor;

    public ChessBoardDao(MySqlConnector mySqlConnector) {
        this.statementExecutor = new StatementExecutor(mySqlConnector);
    }

    public Optional<NewChessBoardDto> save(Turn turn) {
        var query = "INSERT INTO chess_board(game_result, turn) VALUES(?, ?)";
        String[] keys = {"id"};
        ParameterBinder parameterBinder = preparedStatement -> {
            preparedStatement.setString(1, GameResult.IN_PROGRESS.name());
            preparedStatement.setString(2, turn.getSide().name());
        };
        ResultSetMapper<Optional<NewChessBoardDto>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                var id = resultSet.getLong(1);
                return Optional.of(new NewChessBoardDto(id, turn));
            }
            return Optional.empty();
        };
        return statementExecutor.executeUpdate(query, keys, parameterBinder, resultSetMapper);
    }

    public Optional<Turn> findTurnByChessBoardId(long chessBoardId) {
        var query = "SELECT turn FROM chess_board WHERE id = ?";
        ParameterBinder parameterBinder = preparedStatement -> preparedStatement.setLong(1, chessBoardId);
        ResultSetMapper<Optional<Turn>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                var turnAttribute = resultSet.getString("turn");
                Turn turn = Turn.from(Side.valueOf(turnAttribute));
                return Optional.of(turn);
            }
            return Optional.empty();
        };
        return statementExecutor.executeQuery(query, parameterBinder, resultSetMapper);
    }

    public void updateTurn(long chessBoardId, Turn turn) {
        var query = "UPDATE chess_board SET turn = ? WHERE id = ?";
        ParameterBinder parameterBinder = preparedStatement -> {
            preparedStatement.setString(1, turn.getSide().name());
            preparedStatement.setLong(2, chessBoardId);
        };
        statementExecutor.executeUpdate(query, parameterBinder);
    }

    public Optional<LatestChessBoardDto> findLatest() {
        var query = """
                 SELECT p.*, cb.turn FROM chess_board cb
                 JOIN piece p ON p.chess_board_id = cb.id
                 WHERE cb.created_at = (SELECT MAX(created_at) FROM chess_board) AND cb.game_result = ?
                """;
        ParameterBinder parameterBinder = preparedStatement ->
                preparedStatement.setString(1, GameResult.IN_PROGRESS.name());
        ResultSetMapper<Optional<LatestChessBoardDto>> resultSetMapper = resultSet -> {
            if (resultSet.next()) {
                return convertChessBoard(resultSet);
            }
            return Optional.empty();
        };
        return statementExecutor.executeQuery(query, parameterBinder, resultSetMapper);
    }

    private Optional<LatestChessBoardDto> convertChessBoard(ResultSet resultSet) throws SQLException {
        Map<Position, Piece> board = new HashMap<>();
        long chessBoardId = resultSet.getLong("chess_board_id");
        Turn turn = convertTurn(resultSet);
        do {
            Position position = convertPosition(resultSet);
            Piece piece = convertPiece(resultSet);
            board.put(position, piece);
        } while (resultSet.next());
        ChessBoard chessBoard = new ChessBoard(chessBoardId, board);
        return Optional.of(new LatestChessBoardDto(chessBoard, turn));
    }

    private Turn convertTurn(ResultSet resultSet) throws SQLException {
        var turnAttribute = resultSet.getString("turn");
        Side side = Side.valueOf(turnAttribute);
        return Turn.from(side);
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
        var query = "UPDATE chess_board SET game_result = ? WHERE id = ?";
        ParameterBinder parameterBinder = preparedStatement -> {
            preparedStatement.setString(1, gameResult.name());
            preparedStatement.setLong(2, chessBoardId);
        };
        statementExecutor.executeUpdate(query, parameterBinder);
    }

    public List<GameResultDto> findAllGameResult() {
        var query = "SELECT * FROM chess_board WHERE game_result != ? ORDER BY created_at DESC";
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
