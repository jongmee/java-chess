package chess.dao;

import chess.dao.exception.DataAccessException;
import chess.model.evaluation.PieceValue;
import chess.model.piece.Pawn;
import chess.model.piece.Piece;
import chess.model.piece.Queen;
import chess.model.piece.Side;
import chess.model.position.Path;
import chess.model.position.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PieceMapperTest {

    @Test
    @DisplayName("Piece에 대응되는 DB 속성 값이 없으면 예외가 발생한다.")
    void fromWithInvalidPiece() {
        // given
        Piece piece = new Piece(Side.WHITE) {
            @Override
            public Path findPath(Position source, Position target, Piece targetPiece) {
                return null;
            }

            @Override
            public PieceValue value() {
                return null;
            }
        };

        // when & then
        assertThatThrownBy(() -> PieceMapper.from(piece))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("Piece에 대응되는 PieceMapper를 구한다.")
    void from() {
        // given
        Piece pawn = Pawn.from(Side.WHITE);

        // when
        PieceMapper pieceMapper = PieceMapper.from(pawn);

        // then
        assertThat(pieceMapper).isEqualTo(PieceMapper.WHITE_PAWN);
    }

    @Test
    @DisplayName("DB 속성에 대응되는 Piece가 없으면 예외가 발생한다.")
    void mapToPieceWithInvalidAttributes() {
        // given
        String typeAttribute = "Queen";
        String sideAttribute = "NONE";

        // when & then
        assertThatThrownBy(() -> PieceMapper.mapToPiece(typeAttribute, sideAttribute))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("DB 속성에 대응되는 Piece를 구한다.")
    void mapToPiece() {
        // given
        String typeAttribute = "Queen";
        String sideAttribute = "WHITE";

        // when
        Piece piece = PieceMapper.mapToPiece(typeAttribute, sideAttribute);

        // then
        assertThat(piece).isEqualTo(Queen.from(Side.WHITE));
    }
}
