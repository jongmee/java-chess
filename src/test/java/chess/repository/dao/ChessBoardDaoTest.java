package chess.repository.dao;

import chess.model.board.ChessBoard;
import chess.model.board.ChessBoardInitializer;
import chess.model.evaluation.GameResult;
import chess.repository.DataBaseCleaner;
import chess.repository.dto.GameResultDto;
import chess.repository.utility.MySqlConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ChessBoardDaoTest {
    private final DataBaseCleaner dataBaseCleaner = new DataBaseCleaner();
    private final ChessBoardDao chessBoardDao = new ChessBoardDao(MySqlConnector.TEST_CONNECTION);
    private final PieceDao pieceDao = new PieceDao(MySqlConnector.TEST_CONNECTION);

    @BeforeEach
    void setUp() {
        dataBaseCleaner.truncateTables();
    }

    @Test
    @DisplayName("새로운 체스 보드를 저장한다.")
    void save() {
        // when
        Optional<Long> chessBoardId = chessBoardDao.save();

        // then
        assertThat(chessBoardId).isNotEmpty();
    }

    @Test
    @DisplayName("가장 최근 저장된 체스 보드를 조회한다.")
    void findLastId() {
        // given
        long chessBoardId = chessBoardDao.save().get();
        pieceDao.saveAll(new ChessBoardInitializer().create(), chessBoardId);

        // when
        Optional<ChessBoard> chessBoard = chessBoardDao.findLatest();

        // then
        assertAll(
                () -> assertThat(chessBoard).isNotEmpty(),
                () -> chessBoard.ifPresent(actual -> assertThat(actual.getBoard()).hasSize(64))
        );
    }

    @Test
    @DisplayName("체스 보드의 게임 결과를 저장한다")
    void updateGameResult() {
        // given
        long chessBoardId = chessBoardDao.save().get();
        pieceDao.saveAll(new ChessBoardInitializer().create(), chessBoardId);

        // when
        chessBoardDao.updateGameResult(chessBoardId, GameResult.WHITE_WIN);

        // then
        List<GameResultDto> gameResults = chessBoardDao.findAllGameResult();
        assertThat(gameResults).hasSize(1)
                .extracting(GameResultDto::gameResult)
                .contains(GameResult.WHITE_WIN);
    }
}
