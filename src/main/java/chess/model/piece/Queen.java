package chess.model.piece;

import chess.model.position.ChessPosition;
import chess.model.position.Movement;
import chess.model.position.Path;

import java.util.Arrays;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class Queen extends Piece {
    private static final Map<Side, Queen> CACHE = Arrays.stream(Side.values())
            .collect(toMap(identity(), Queen::new));

    private Queen(Side side) {
        super(side);
    }

    public static Queen from(Side side) {
        return CACHE.get(side);
    }

    @Override
    public Path findPath(ChessPosition source, ChessPosition target, Piece targetPiece) {
        checkValidTargetPiece(targetPiece);
        Movement movement = target.calculateMovement(source);
        if (canMove(movement)) {
            return Path.makeStraightPath(source, movement);
        }
        return Path.empty();
    }

    private boolean canMove(Movement movement) {
        return movement.isOrthogonal() || movement.isDiagonal();
    }
}
