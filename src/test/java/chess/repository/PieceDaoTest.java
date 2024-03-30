package chess.repository;

import chess.model.board.ChessBoard;
import chess.model.board.ChessBoardInitializer;
import chess.model.piece.Knight;
import chess.model.piece.Piece;
import chess.model.piece.Side;
import chess.model.position.File;
import chess.model.position.Position;
import chess.model.position.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PieceDaoTest {
    private final DataBaseCleaner dataBaseCleaner = new DataBaseCleaner();
    private final PieceDao pieceDao = PieceDao.INSTANCE;
    private final ChessBoardDao chessBoardDao = ChessBoardDao.INSTANCE;

    @BeforeEach
    void setUp() {
        dataBaseCleaner.truncateTables();
    }

    @Test
    @DisplayName("체스 보드의 기물들을 저장한다.")
    void saveAll() {
        // given
        Map<Position, Piece> initialChessBoard = new ChessBoardInitializer().create();
        int chessBoardId = chessBoardDao.save().get();

        // when
        pieceDao.saveAll(initialChessBoard, chessBoardId);

        // then
        ChessBoard chessBoard = chessBoardDao.findById(chessBoardId);
        assertThat(chessBoard.getBoard()).hasSize(64);
    }

    @Test
    @DisplayName("특정 위치의 기물을 교체한다.")
    void update() {
        // given
        int chessBoardId = chessBoardDao.save().get();
        Map<Position, Piece> initialChessBoard = new ChessBoardInitializer().create();
        pieceDao.saveAll(initialChessBoard, chessBoardId);

        Position position = Position.of(File.A, Rank.FIVE);
        Piece newPiece = Knight.from(Side.WHITE);

        // when
        pieceDao.update(chessBoardId, position, newPiece);

        // then
        ChessBoard chessBoard = chessBoardDao.findById(chessBoardId);
        assertThat(chessBoard.getBoard()).containsEntry(position, newPiece);
    }
}
