package chess.repository.dao;

import chess.model.board.Turn;
import chess.model.piece.Side;
import chess.repository.DataBaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TurnDaoTest {
    private final DataBaseCleaner dataBaseCleaner = new DataBaseCleaner();
    private final TurnDao turnDao = new TurnDao();
    private final ChessBoardDao chessBoardDao = new ChessBoardDao();

    @BeforeEach
    void setUp() {
        dataBaseCleaner.truncateTables();
    }

    @Test
    @DisplayName("체스 게임의 차례를 저장한다.")
    void save() {
        // given
        long chessBoardId = chessBoardDao.save().get();
        Turn turn = Turn.from(Side.WHITE);

        // when
        turnDao.save(chessBoardId, turn);

        // then
        Optional<Turn> savedTurn = turnDao.findByChessBoardId(chessBoardId);
        assertThat(savedTurn).contains(turn);
    }

    @Test
    @DisplayName("체스 게임의 차례를 삭제한다.")
    void delete() {
        // given
        long chessBoardId = chessBoardDao.save().get();
        Turn turn = Turn.from(Side.WHITE);
        turnDao.save(chessBoardId, turn);

        // when
        turnDao.delete(chessBoardId);

        // then
        Optional<Turn> savedTurn = turnDao.findByChessBoardId(chessBoardId);
        assertThat(savedTurn).isEmpty();
    }
}
