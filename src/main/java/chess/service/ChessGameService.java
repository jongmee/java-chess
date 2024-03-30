package chess.service;

import chess.model.board.ChessBoard;
import chess.model.board.ChessBoardGenerator;
import chess.model.board.ChessBoardInitializer;
import chess.model.board.Turn;
import chess.model.piece.Blank;
import chess.model.piece.Piece;
import chess.model.piece.Side;
import chess.model.position.Position;
import chess.repository.dao.ChessBoardDao;
import chess.repository.dao.PieceDao;
import chess.repository.dao.TurnDao;
import chess.repository.exception.DataAccessException;
import chess.view.input.MoveArguments;

import java.util.Map;

public class ChessGameService {
    public static final ChessGameService INSTANCE = new ChessGameService();

    private final ChessBoardDao chessBoardDao = ChessBoardDao.INSTANCE;
    private final PieceDao pieceDao = PieceDao.INSTANCE;
    private final TurnDao turnDao = TurnDao.INSTANCE;

    private ChessGameService() {
    }

    public ChessBoard saveInitialChessBoard() {
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

    public Turn saveInitialTurn(ChessBoard chessBoard) {
        long chessBoardId = chessBoard.getId();
        Turn initialTurn = Turn.from(Side.WHITE);
        turnDao.save(chessBoardId, initialTurn);
        return initialTurn;
    }

    public Turn saveNextTurn(ChessBoard chessBoard, Turn turn) {
        long chessBoardId = chessBoard.getId();
        Turn nextTurn = turn.getNextTurn();
        turnDao.save(chessBoardId, nextTurn);
        return nextTurn;
    }
}
