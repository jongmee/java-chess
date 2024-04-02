package chess.dao;

import chess.model.piece.*;
import chess.dao.exception.DataAccessException;

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

    private final String typeAttribute;
    private final String sideAttribute;
    private final Piece piece;

    PieceMapper(String typeAttribute, String sideAttribute, Piece piece) {
        this.typeAttribute = typeAttribute;
        this.sideAttribute = sideAttribute;
        this.piece = piece;
    }

    public static PieceMapper from(Piece piece) {
        return Arrays.stream(values())
                .filter(pieceMapper -> pieceMapper.piece.equals(piece))
                .findFirst()
                .orElseThrow(() -> new DataAccessException("Piece에 대응되는 DB 속성 값이 없습니다."));
    }

    public static Piece mapToPiece(String typeAttribute, String sideAttribute) {
        return Arrays.stream(values())
                .filter(pieceMapper -> pieceMapper.typeAttribute.equals(typeAttribute) && pieceMapper.sideAttribute.equals(sideAttribute))
                .findFirst()
                .orElseThrow(() -> new DataAccessException("DB 속성에 대응되는 Piece가 없습니다."))
                .piece;
    }

    public String typeAttribute() {
        return typeAttribute;
    }

    public String sideAttribute() {
        return sideAttribute;
    }
}
