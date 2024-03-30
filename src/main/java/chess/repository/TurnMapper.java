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
                .orElseThrow()
                .turn;
    }

    public static String mapToSideAttribute(Turn turn) {
        return Arrays.stream(values())
                .filter(turnMapper -> turnMapper.turn.equals(turn))
                .findFirst()
                .orElseThrow()
                .sideAttribute;
    }
}
