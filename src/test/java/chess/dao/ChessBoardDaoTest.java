package chess.dao;

import chess.dao.dto.GameResultDto;
import chess.dao.dto.LatestChessBoardDto;
import chess.dao.dto.NewChessBoardDto;
import chess.dao.util.TestMySqlConnector;
import chess.model.board.ChessBoardInitializer;
import chess.model.board.Turn;
import chess.model.evaluation.GameResult;
import chess.model.piece.Side;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ChessBoardDaoTest {
    private final DataBaseCleaner dataBaseCleaner = new DataBaseCleaner();
    private final ChessBoardDao chessBoardDao = new ChessBoardDao(TestMySqlConnector.INSTANCE);
    private final PieceDao pieceDao = new PieceDao(TestMySqlConnector.INSTANCE);

    @AfterEach
    void setUp() {
        dataBaseCleaner.truncateTables();
    }

    @Test
    @DisplayName("새로운 체스 보드를 저장한다.")
    void save() {
        // when
        Optional<NewChessBoardDto> newChessBoardDto = chessBoardDao.save(Turn.from(Side.WHITE));

        // then
        assertAll(
                () -> assertThat(newChessBoardDto).isNotEmpty(),
                () -> assertThat(newChessBoardDto.get().id()).isSameAs(1L),
                () -> assertThat(newChessBoardDto.get().turn()).isEqualTo(Turn.from(Side.WHITE))
        );
    }

    @Test
    @DisplayName("체스 보드의 게임 차례를 수정한다.")
    void updateTurn() {
        // given
        NewChessBoardDto newChessBoardDto = chessBoardDao.save(Turn.from(Side.WHITE)).get();
        long chessBoardId = newChessBoardDto.id();

        // when
        chessBoardDao.updateTurn(chessBoardId, Turn.from(Side.BLACK));

        // then
        Optional<Turn> nextTurn = chessBoardDao.findTurnByChessBoardId(chessBoardId);
        assertAll(
                () -> assertThat(nextTurn).isPresent(),
                () -> assertThat(nextTurn.get()).isEqualTo(Turn.from(Side.BLACK))
        );
    }

    @Test
    @DisplayName("가장 최근 저장된 체스 보드를 조회한다.")
    void findLatest() {
        // given
        NewChessBoardDto newChessBoardDto = chessBoardDao.save(Turn.from(Side.WHITE)).get();
        long chessBoardId = newChessBoardDto.id();
        pieceDao.saveAll(new ChessBoardInitializer().create(), chessBoardId);

        // when
        Optional<LatestChessBoardDto> initialChessBoardDto = chessBoardDao.findLatest();

        // then
        assertAll(
                () -> assertThat(initialChessBoardDto).isNotEmpty(),
                () -> assertThat(initialChessBoardDto.get().turn()).isNotNull(),
                () -> assertThat(initialChessBoardDto.get().chessBoard().getBoard()).hasSize(64)
        );
    }

    @Test
    @DisplayName("체스 보드의 게임 결과를 저장한다")
    void updateGameResult() {
        // given
        NewChessBoardDto newChessBoardDto = chessBoardDao.save(Turn.from(Side.WHITE)).get();
        long chessBoardId = newChessBoardDto.id();
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
