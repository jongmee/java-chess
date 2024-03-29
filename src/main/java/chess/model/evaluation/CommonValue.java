package chess.model.evaluation;

import chess.model.position.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonValue implements PieceValue {
    private static final Map<Double, CommonValue> CACHE = new HashMap<>();

    private final double point;

    private CommonValue(double point) {
        this.point = point;
    }

    public static CommonValue from(double point) {
        if (!CACHE.containsKey(point)) {
            CACHE.put(point, new CommonValue(point));
        }
        return CACHE.get(point);
    }

    @Override
    public double calculateValue(List<Position> positions) {
        return positions.size() * point;
    }
}
