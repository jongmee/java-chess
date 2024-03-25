package chess.model.piece;

import chess.model.position.ChessPosition;
import chess.model.position.Path;

import java.util.Objects;

public abstract class Piece {
    protected final Side side;

    protected Piece(Side side) {
        this.side = side;
    }

    public abstract Path findPath(ChessPosition source, ChessPosition target, Piece targetPiece);

    public final boolean isSameSide(Piece other) {
        return this.side == other.side;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return side == piece.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(side);
    }
}
