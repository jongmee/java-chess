package chess.model.piece;

public enum Side {
    BLACK,
    WHITE;

    public boolean isWhite() {
        return this.equals(WHITE);
    }

    public boolean isBlack() {
        return this.equals(BLACK);
    }
}
