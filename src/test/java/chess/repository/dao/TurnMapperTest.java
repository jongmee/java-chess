package chess.repository.dao;

import chess.model.board.Turn;
import chess.model.piece.Side;
import chess.repository.exception.DataAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TurnMapperTest {

    @Test
    @DisplayName("DB 속성에 대응되는 Turn이 없으면 예외가 발생한다.")
    void mapToTurnWithInvalidAttribute() {
        // given
        String sideAttribute = "NONE";

        // when & then
        assertThatThrownBy(() -> TurnMapper.mapToTurn(sideAttribute))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("DB 속성에 대응되는 Turn을 구한다.")
    void mapToTurn() {
        // given
        String sideAttribute = "WHITE";

        // when
        Turn turn = TurnMapper.mapToTurn(sideAttribute);

        // then
        assertThat(turn).isEqualTo(Turn.from(Side.WHITE));
    }

    @Test
    @DisplayName("Turn에 대응되는 DB 속성 값이 없으면 예외가 발생한다.")
    void mapToSideAttributeWithInvalidTurn() {
        // given
        Turn turn = Turn.from(Side.NONE);

        // when & then
        assertThatThrownBy(() -> TurnMapper.mapToSideAttribute(turn))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("Turn에 대응되는 속성 값을 구한다.")
    void mapToSideAttribute() {
        // given
        Turn turn = Turn.from(Side.WHITE);

        // when
        String sideAttribute = TurnMapper.mapToSideAttribute(turn);

        // then
        assertThat(sideAttribute).isEqualTo("WHITE");
    }
}
