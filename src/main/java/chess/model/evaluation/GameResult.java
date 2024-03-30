package chess.model.evaluation;

import chess.model.piece.Side;

public enum GameResult {
    IN_PROGRESS,
    WHITE_WIN,
    BLACK_WIN,
    TIE;

    public static GameResult from(Side side) {
        if (side.isWhite()) {
            return WHITE_WIN;
        }
        if (side.isBlack()) {
            return BLACK_WIN;
        }
        throw new IllegalArgumentException("BLACK 혹은 WHITE 진영만 결과를 판단할 수 있습니다.");
    }
}
