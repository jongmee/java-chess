package chess.model.piece;

public enum Side {
    BLACK,
    WHITE,
    NONE;

    public boolean isWhite() {
        return this == WHITE;
    }

    public boolean isBlack() {
        return this == BLACK;
    }

    public Side getOppositeSide() {
        if (isWhite()) {
            return BLACK;
        }
        if (isBlack()) {
            return WHITE;
        }
        return NONE;
    }
}
