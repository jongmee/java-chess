package chess.dao;

import chess.dao.dto.NewChessBoardDto;
import chess.dao.util.TestMySqlConnector;
import chess.model.board.ChessBoard;
import chess.model.board.ChessBoardInitializer;
import chess.model.board.Turn;
import chess.model.piece.Knight;
import chess.model.piece.Piece;
import chess.model.piece.Side;
import chess.model.position.File;
import chess.model.position.Position;
import chess.model.position.Rank;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PieceDaoTest {
    private final DataBaseCleaner dataBaseCleaner = new DataBaseCleaner();
    private final PieceDao pieceDao = new PieceDao(TestMySqlConnector.INSTANCE);
    private final ChessBoardDao chessBoardDao = new ChessBoardDao(TestMySqlConnector.INSTANCE);

    @AfterEach
    void setUp() {
        dataBaseCleaner.truncateTables();
    }

    @Test
    @DisplayName("체스 보드의 기물들을 저장한다.")
    void saveAll() {
        // given
        Map<Position, Piece> initialPieces = new ChessBoardInitializer().create();
        NewChessBoardDto newChessBoardDto = chessBoardDao.save(Turn.from(Side.WHITE)).get();

        // when
        pieceDao.saveAll(initialPieces, newChessBoardDto.id());

        // then
        ChessBoard chessBoard = chessBoardDao.findLatest().get().chessBoard();
        assertThat(chessBoard.getBoard()).hasSize(64);
    }

    @Test
    @DisplayName("특정 위치의 기물을 교체한다.")
    void update() {
        // given
        NewChessBoardDto newChessBoardDto = chessBoardDao.save(Turn.from(Side.WHITE)).get();
        Map<Position, Piece> initialPieces = new ChessBoardInitializer().create();
        pieceDao.saveAll(initialPieces, newChessBoardDto.id());

        Position position = Position.of(File.A, Rank.FIVE);
        Piece newPiece = Knight.from(Side.WHITE);

        // when
        pieceDao.update(newChessBoardDto.id(), position, newPiece);

        // then
        ChessBoard chessBoard = chessBoardDao.findLatest().get().chessBoard();
        assertThat(chessBoard.getBoard()).containsEntry(position, newPiece);
    }
}
