package chess.repository;

import chess.model.board.Turn;
import chess.model.piece.Side;

import java.util.Arrays;

public enum TurnMapper {
    BLACK_TURN(Turn.from(Side.BLACK), "BLACK"),
    WHITE_TURN(Turn.from(Side.WHITE), "WHITE");

    private final Turn turn;
    private final String sideAttribute;

    TurnMapper(Turn turn, String sideAttribute) {
        this.turn = turn;
        this.sideAttribute = sideAttribute;
    }

    public static Turn mapToTurn(String sideAttribute) {
        return Arrays.stream(values())
                .filter(turnMapper -> turnMapper.sideAttribute.equals(sideAttribute))
                .findFirst()
                .orElseThrow(() -> new DataAccessException("DB 속성에 대응되는 Turn이 없습니다."))
                .turn;
    }

    public static String mapToSideAttribute(Turn turn) {
        return Arrays.stream(values())
                .filter(turnMapper -> turnMapper.turn.equals(turn))
                .findFirst()
                .orElseThrow(() -> new DataAccessException("Turn에 대응되는 DB 속성이 없습니다."))
                .sideAttribute;
    }
}
