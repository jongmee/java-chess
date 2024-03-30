package chess.repository;

import chess.model.board.ChessBoard;
import chess.model.board.ChessBoardInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ChessBoardDaoTest {
    private final DataBaseCleaner dataBaseCleaner = new DataBaseCleaner();
    private final ChessBoardDao chessBoardDao = ChessBoardDao.INSTANCE;

    @BeforeEach
    void setUp() {
        dataBaseCleaner.truncateTables();
    }

    @Test
    @DisplayName("새로운 체스 보드를 저장한다.")
    void save() {
        // when
        Optional<Integer> chessBoardId = chessBoardDao.save();

        // then
        assertThat(chessBoardId).isNotEmpty();
    }

    @Test
    @DisplayName("체스 보드를 id로 조회한다.")
    void findById() {
        // given
        int chessBoardId = chessBoardDao.save().get();
        PieceDao pieceDao = PieceDao.INSTANCE;
        pieceDao.saveAll(new ChessBoardInitializer().create(), chessBoardId);

        // when
        ChessBoard chessBoard = chessBoardDao.findById(chessBoardId);

        // then
        assertThat(chessBoard.getBoard()).hasSize(64);
    }
}
