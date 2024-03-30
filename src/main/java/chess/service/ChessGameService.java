package chess.service;

import chess.model.board.ChessBoard;
import chess.model.board.ChessBoardGenerator;
import chess.model.board.ChessBoardInitializer;
import chess.model.board.Turn;
import chess.model.evaluation.GameResult;
import chess.model.piece.Blank;
import chess.model.piece.Piece;
import chess.model.piece.Side;
import chess.model.position.Position;
import chess.repository.dao.ChessBoardDao;
import chess.repository.dao.PieceDao;
import chess.repository.dao.TurnDao;
import chess.repository.dto.GameResultDto;
import chess.repository.exception.DataAccessException;
import chess.repository.utility.MySqlConnector;
import chess.view.input.MoveArguments;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChessGameService {
    private final ChessBoardDao chessBoardDao;
    private final PieceDao pieceDao;
    private final TurnDao turnDao;

    public ChessGameService(MySqlConnector mySqlConnector) {
        this.chessBoardDao = new ChessBoardDao(mySqlConnector);
        this.pieceDao = new PieceDao(mySqlConnector);
        this.turnDao = new TurnDao(mySqlConnector);
    }

    public ChessBoard createOrGetInitialChessBoard() {
        Optional<ChessBoard> chessBoard = chessBoardDao.findLatest();
        return chessBoard.orElseGet(this::createInitialChessBoard);
    }

    private ChessBoard createInitialChessBoard() {
        ChessBoardGenerator chessBoardInitializer = new ChessBoardInitializer();
        Map<Position, Piece> allPieces = chessBoardInitializer.create();

        long chessBoardId = chessBoardDao.save()
                .orElseThrow(() -> new DataAccessException("체스 보드가 저장되지 못했습니다."));
        pieceDao.saveAll(allPieces, chessBoardId);
        return new ChessBoard(chessBoardId, allPieces);
    }

    public void move(ChessBoard chessBoard, Turn turn, MoveArguments moveArguments) {
        Position sourcePosition = moveArguments.createSourcePosition();
        Position targetPosition = moveArguments.createTargetPosition();
        Piece sourcePiece = chessBoard.move(sourcePosition, targetPosition, turn);
        pieceDao.update(chessBoard.getId(), sourcePosition, Blank.INSTANCE);
        pieceDao.update(chessBoard.getId(), targetPosition, sourcePiece);
    }

    public Turn createOrGetInitialTurn(ChessBoard chessBoard) {
        long chessBoardId = chessBoard.getId();
        Optional<Turn> turn = turnDao.findByChessBoardId(chessBoardId);
        return turn.orElseGet(() -> createInitialTurn(chessBoardId));
    }

    private Turn createInitialTurn(long chessBoardId) {
        Turn initialTurn = Turn.from(Side.WHITE);
        turnDao.save(chessBoardId, initialTurn);
        return initialTurn;
    }

    public Turn saveNextTurn(ChessBoard chessBoard, Turn turn) {
        long chessBoardId = chessBoard.getId();
        Turn nextTurn = turn.getNextTurn();
        turnDao.delete(chessBoardId);
        turnDao.save(chessBoardId, nextTurn);
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
