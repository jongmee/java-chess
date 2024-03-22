package chess.model.board;

import chess.model.piece.*;
import chess.model.position.ChessPosition;
import chess.model.position.File;
import chess.model.position.Rank;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class ChessBoardInitializer {
    private static final int FIRST_BLANK_RANK_COORDINATE = 3;
    private static final int LAST_BLANK_RANK_COORDINATE = 6;

    public ChessBoardInitializer() {
    }

    public Map<ChessPosition, Piece> create() {
        Map<ChessPosition, Piece> board = new HashMap<>();
        board.putAll(createSpecialPieces(Side.BLACK));
        board.putAll(createPawns(Side.BLACK));
        board.putAll(createSpecialPieces(Side.WHITE));
        board.putAll(createPawns(Side.WHITE));
        board.putAll(createBlanks());
        return unmodifiableMap(board);
    }

    private Map<ChessPosition, Piece> createSpecialPieces(Side side) {
        Rank rank = convertSpecialPieceRankWithSide(side);
        return Map.of(
                ChessPosition.of(File.A, rank), Rook.from(side),
                ChessPosition.of(File.B, rank), Knight.from(side),
                ChessPosition.of(File.C, rank), Bishop.from(side),
                ChessPosition.of(File.D, rank), Queen.from(side),
                ChessPosition.of(File.E, rank), King.from(side),
                ChessPosition.of(File.F, rank), Bishop.from(side),
                ChessPosition.of(File.G, rank), Knight.from(side),
                ChessPosition.of(File.H, rank), Rook.from(side)
        );
    }

    private Rank convertSpecialPieceRankWithSide(Side side) {
        if (side == Side.BLACK) {
            return Rank.EIGHT;
        }
        return Rank.ONE;
    }

    private Map<ChessPosition, Piece> createPawns(Side side) {
        Rank rank = convertPawnRanksWithSide(side);
        return Arrays.stream(File.values())
                .collect(toMap(
                        file -> ChessPosition.of(file, rank),
                        file -> Pawn.from(side))
                );
    }

    private Rank convertPawnRanksWithSide(Side side) {
        if (side == Side.BLACK) {
            return Rank.SEVEN;
        }
        return Rank.TWO;
    }

    public Map<ChessPosition, Piece> createBlanks() {
        return IntStream.range(FIRST_BLANK_RANK_COORDINATE, LAST_BLANK_RANK_COORDINATE + 1)
                .boxed()
                .flatMap(rankCoordinate -> Arrays.stream(File.values())
                        .map(file -> ChessPosition.of(file, Rank.from(rankCoordinate)))
                ).collect(toMap(
                        identity(),
                        chessPosition -> Blank.INSTANCE)
                );
    }
}
