package chess.model.evaluation;

import chess.model.piece.Side;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameResultTest {

    @Test
    @DisplayName("BLACK 혹은 WHITE 진영이 아니면 결과 생성 시 예외가 발생한다.")
    void from() {
        // given
        Side noneSide = Side.NONE;

        // when & then
        assertThatThrownBy(() -> GameResult.from(noneSide))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
