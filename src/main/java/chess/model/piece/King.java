package chess.model.piece;

import chess.model.position.ChessPosition;
import chess.model.position.Movement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class King extends Piece {
    private static final Map<Side, King> CACHE = Arrays.stream(Side.values())
            .collect(toMap(identity(), King::new));

    private static final int DISPLACEMENT = 1;

    private King(Side side) {
        super(side);
    }

    public static King from(Side side) {
        return CACHE.get(side);
    }

    @Override
    public List<ChessPosition> findPath(ChessPosition source, ChessPosition target, Piece targetPiece) {
        checkValidTargetPiece(targetPiece);
        Movement movement = target.calculateMovement(source);
        if (movement.hasLengthOf(DISPLACEMENT)) {
            return List.of(target);
        }
        return List.of();
    }
}
