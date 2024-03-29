package chess.model.evaluation;

import chess.model.position.File;
import chess.model.position.Position;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class PawnValue implements PieceValue {
    private static final double POINT = 1;
    private static final double UNFAVORABLE_POINT = 0.5;
    private static final int UNFAVORABLE_POINT_THRESHOLD = 1;
    public static final PawnValue INSTANCE = new PawnValue();

    private PawnValue() {
    }

    @Override
    public double calculateValue(List<Position> positions) {
        Map<File, Long> countByFile = positions.stream()
                .collect(groupingBy(Position::getFile, counting()));

        return countByFile.values()
                .stream()
                .mapToDouble(this::pointOfCount)
                .sum();
    }

    private double pointOfCount(long count) {
        if (count > UNFAVORABLE_POINT_THRESHOLD) {
            return count * UNFAVORABLE_POINT;
        }
        return count * POINT;
    }
}
