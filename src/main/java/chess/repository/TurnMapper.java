package chess.repository;

import chess.model.board.Turn;
import chess.model.piece.Side;

import java.util.Arrays;

public enum TurnMapper {
    BLACK_TURN(Turn.from(Side.BLACK), "BLACK"),
    WHITE_TURN(Turn.from(Side.WHITE), "WHITE");

    private final Turn turn;
    private final String side;

    TurnMapper(Turn turn, String side) {
        this.turn = turn;
        this.side = side;
    }

    public static Turn findTurnBySide(String side) {
        return Arrays.stream(values())
                .filter(turnMapper -> turnMapper.side.equals(side))
                .findFirst()
                .orElseThrow()
                .turn;
    }

    public static String findSideByTurn(Turn turn) {
        return Arrays.stream(values())
                .filter(turnMapper -> turnMapper.turn.equals(turn))
                .findFirst()
                .orElseThrow()
                .side;
    }
}
