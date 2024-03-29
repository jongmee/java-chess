package chess.model.piece;

import chess.model.evaluation.CommonValue;
import chess.model.evaluation.PieceValue;
import chess.model.position.Movement;
import chess.model.position.Path;
import chess.model.position.Position;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class Knight extends Piece {
    private static final Map<Side, Knight> CACHE = Side.colors()
            .stream()
            .collect(toMap(identity(), Knight::new));

    private static final PieceValue VALUE = CommonValue.from(2.5);

    private static final int DISPLACEMENT = 3;

    private Knight(Side side) {
        super(side);
    }

    public static Knight from(Side side) {
        return CACHE.get(side);
    }

    @Override
    public Path findPath(Position source, Position target, Piece targetPiece) {
        Movement movement = target.calculateMovement(source);
        if (canMove(movement)) {
            return new Path(List.of(target));
        }
        return Path.empty();
    }

    private boolean canMove(Movement movement) {
        if (movement.isOrthogonal() || movement.isDiagonal()) {
            return false;
        }
        return movement.hasLengthOf(DISPLACEMENT);
    }

    @Override
    public PieceValue value() {
        return VALUE;
    }
}
