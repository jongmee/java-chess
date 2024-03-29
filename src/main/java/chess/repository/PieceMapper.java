package chess.repository;

import chess.model.piece.*;

import java.util.Arrays;

public enum PieceMapper {
    WHITE_BISHOP("Bishop", "WHITE", Bishop.from(Side.WHITE)),
    BLACK_BISHOP("Bishop", "BLACK", Bishop.from(Side.BLACK)),
    WHITE_KING("King", "WHITE", King.from(Side.WHITE)),
    BLACK_KING("King", "BLACK", King.from(Side.BLACK)),
    WHITE_KNIGHT("Knight", "WHITE", Knight.from(Side.WHITE)),
    BLACK_KNIGHT("Knight", "BLACK", Knight.from(Side.BLACK)),
    WHITE_PAWN("Pawn", "WHITE", Pawn.from(Side.WHITE)),
    BLACK_PAWN("Pawn", "BLACK", Pawn.from(Side.BLACK)),
    WHITE_QUEEN("Queen", "WHITE", Queen.from(Side.WHITE)),
    BLACK_QUEEN("Queen", "BLACK", Queen.from(Side.BLACK)),
    WHITE_ROOK("Rook", "WHITE", Rook.from(Side.WHITE)),
    BLACK_ROOK("Rook", "BLACK", Rook.from(Side.BLACK)),
    BLANK("Blank", "NONE", Blank.INSTANCE);

    private final String type;
    private final String side;
    private final Piece piece;

    PieceMapper(String type, String side, Piece piece) {
        this.type = type;
        this.side = side;
        this.piece = piece;
    }

    public static PieceMapper from(Piece piece) {
        return Arrays.stream(values())
                .filter(pieceMapper -> pieceMapper.piece.equals(piece))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(""));
    }

    public static Piece findPieceByTypeAndSide(String type, String side) {
        return Arrays.stream(values())
                .filter(pieceMapper -> pieceMapper.type.equals(type) && pieceMapper.side.equals(side))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(""))
                .piece;
    }

    public String type() {
        return type;
    }

    public String side() {
        return side;
    }
}
