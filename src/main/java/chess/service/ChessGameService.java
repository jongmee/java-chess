package chess.service;

import chess.model.board.ChessBoard;
import chess.model.board.ChessBoardInitializer;
import chess.model.board.Turn;
import chess.model.evaluation.GameResult;
import chess.model.piece.Blank;
import chess.model.piece.Piece;
import chess.model.piece.Side;
import chess.model.position.Position;
import chess.dao.ChessBoardDao;
import chess.dao.PieceDao;
import chess.dao.dto.GameResultDto;
import chess.dao.dto.LatestChessBoardDto;
import chess.dao.dto.NewChessBoardDto;
import chess.dao.exception.DataAccessException;
import chess.dao.util.MySqlConnector;
import chess.view.input.MoveArguments;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChessGameService {
    private final ChessBoardDao chessBoardDao;
    private final PieceDao pieceDao;

    public ChessGameService(MySqlConnector mySqlConnector) {
        this.chessBoardDao = new ChessBoardDao(mySqlConnector);
        this.pieceDao = new PieceDao(mySqlConnector);
    }

    public LatestChessBoardDto createOrGetInitialChessBoard() {
        Optional<LatestChessBoardDto> chessBoard = chessBoardDao.findLatest();
        return chessBoard.orElseGet(this::createInitialChessBoard);
    }

    private LatestChessBoardDto createInitialChessBoard() {
        ChessBoardInitializer chessBoardInitializer = new ChessBoardInitializer();
        Map<Position, Piece> allPieces = chessBoardInitializer.create();

        NewChessBoardDto newChessBoardDto = chessBoardDao.save(Turn.from(Side.WHITE))
                .orElseThrow(() -> new DataAccessException("체스 보드가 저장되지 못했습니다."));
        pieceDao.saveAll(allPieces, newChessBoardDto.id());
        ChessBoard chessBoard = new ChessBoard(newChessBoardDto.id(), allPieces);
        return new LatestChessBoardDto(chessBoard, newChessBoardDto.turn());
    }

    public void move(ChessBoard chessBoard, Turn turn, MoveArguments moveArguments) {
        Position sourcePosition = moveArguments.createSourcePosition();
        Position targetPosition = moveArguments.createTargetPosition();
        Piece sourcePiece = chessBoard.move(sourcePosition, targetPosition, turn);
        pieceDao.update(chessBoard.getId(), sourcePosition, Blank.INSTANCE);
        pieceDao.update(chessBoard.getId(), targetPosition, sourcePiece);
    }

    public Turn saveNextTurn(ChessBoard chessBoard, Turn turn) {
        long chessBoardId = chessBoard.getId();
        Turn nextTurn = turn.getNextTurn();
        chessBoardDao.updateTurn(chessBoardId, nextTurn);
        return nextTurn;
    }

    public void saveGameResult(ChessBoard chessBoard) {
        long chessBoardId = chessBoard.getId();
        GameResult gameResult = chessBoard.determineGameResult();
        chessBoardDao.updateGameResult(chessBoardId, gameResult);
    }

    public List<GameResultDto> findAllGameResults() {
        return chessBoardDao.findAllGameResult();
    }
}
